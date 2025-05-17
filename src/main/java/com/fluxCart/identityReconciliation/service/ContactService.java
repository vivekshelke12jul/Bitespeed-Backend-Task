package com.fluxCart.identityReconciliation.service;

import com.fluxCart.identityReconciliation.model.Contact;
import com.fluxCart.identityReconciliation.payload.request.ContactRequest;
import com.fluxCart.identityReconciliation.payload.response.ConsolidatedContactResponse;
import com.fluxCart.identityReconciliation.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public ConsolidatedContactResponse consolidateContacts(ContactRequest req) {

        Contact priContactPhone = getPrimaryContactByPhone(req.getPhoneNumber());
        Contact priContactEmail = getPrimaryContactByEmail(req.getEmail());

//  CASE0: a contact exist with both phone and email as that of the request
        Optional<Contact> oContact = contactRepository.findByPhoneNumberAndEmail(req.getPhoneNumber(), req.getEmail());
        if(oContact.isPresent()) {
            Contact contact = oContact.get();

            // there is no new email or phone number so don't create a contact entity

            // get all secondary contacts of the primary contact
            List<Contact> secondaryContacts = contactRepository.findByLinkedId(oContact.get().getId());

            // consolidate into one contact response
            return new ConsolidatedContactResponse(contact, secondaryContacts);
        }

//  CASE1: if both phone and email are not present in any contact
        if(priContactPhone == null && priContactEmail == null) {

            // create a new contact
            Contact contact = new Contact(
                    req.getPhoneNumber(),
                    req.getEmail(),
                    null,
                    "primary",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );
            Contact savedContact = contactRepository.save(contact);
            return new ConsolidatedContactResponse(savedContact);
        }

//  CASE2: if either phone or email is present
        if(priContactPhone == null || priContactEmail == null) {

            Contact priContact = priContactPhone == null ? priContactEmail : priContactPhone;

            // create a new contact
            Contact contact = new Contact(
                    req.getPhoneNumber(),
                    req.getEmail(),
                    priContact.getId(),
                    "secondary",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );
            Contact savedContact = contactRepository.save(contact);

            // get all secondary contacts of the primary contact
            List<Contact> secondaryContacts = contactRepository.findByLinkedId(priContact.getId());

            // consolidate into one contact response
            return new ConsolidatedContactResponse(priContact, secondaryContacts);
        }

//  CASE3: if both phone and email are linked to the same primary contact
        if(priContactPhone.getId().equals(priContactEmail.getId())) {

            // there is no new email or phone number so don't create a contact entity

            // get all secondary contacts of the primary contact
            List<Contact> secondaryContacts = contactRepository.findByLinkedId(priContactPhone.getId());

            // consolidate into one contact response
            return new ConsolidatedContactResponse(priContactPhone, secondaryContacts);
        }

//  CASE4: if phone and email are linked to different primary contacts
        Contact older = priContactPhone;
        Contact newer = priContactEmail;
        if(priContactEmail.getCreatedAt().isBefore(priContactPhone.getCreatedAt())) {
            older = priContactEmail;
            newer = priContactPhone;
        }

        // there is no new email or phone number so don't create a contact entity

        // the newer and all the contacts linked to newer will be linked to older
        List<Contact> secondaryContactsOfNewer = contactRepository.findByLinkedId(newer.getId());
        for(Contact contact : secondaryContactsOfNewer) {
            contact.setLinkedId(older.getId());
            contact.setUpdatedAt(LocalDateTime.now());
            contactRepository.save(contact);
        }
        newer.setLinkedId(older.getId());
        newer.setLinkPrecedence("secondary");
        newer.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(newer);

        // get all latest secondary contacts of older after updating
        List<Contact> secondaryContacts = contactRepository.findByLinkedId(older.getId());

        // consolidate into one contact response
        return new ConsolidatedContactResponse(older, secondaryContacts);
    }

    private Contact getPrimaryContactByPhone(String phone) {
        if(phone == null || phone.isBlank()) {
            return null;
        }

        Optional<Contact> oContact = contactRepository.findByPhoneNumber(phone);
        if(oContact.isEmpty()) {
            return null;
        }

        // if a contact with this phone is present
        // find if this contact is primary ,else get the primary contact from this contact
        Contact contact = oContact.get();
        Contact priContactPhone = contact.getLinkPrecedence().equals("primary") ? contact : contactRepository.findById(contact.getLinkedId()).get();
        return priContactPhone;
    }

    private Contact getPrimaryContactByEmail(String email) {
        if(email == null || email.isBlank()) {
            return null;
        }

        Optional<Contact> oContact = contactRepository.findByEmail(email);
        if(oContact.isEmpty()) {
            return null;
        }

        // if contact with this email is present
        // find if this contact is primary ,else get the primary contact from this contact
        Contact contact = oContact.get();
        Contact priContactEmail = contact.getLinkPrecedence().equals("primary") ? contact : contactRepository.findById(contact.getLinkedId()).get();
        return priContactEmail;
    }

}
