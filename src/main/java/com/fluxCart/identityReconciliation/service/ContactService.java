package com.fluxCart.identityReconciliation.service;

import com.fluxCart.identityReconciliation.model.Contact;
import com.fluxCart.identityReconciliation.payload.request.ContactRequest;
import com.fluxCart.identityReconciliation.payload.response.ConsolidatedContactResponse;
import com.fluxCart.identityReconciliation.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {


    @Autowired
    private ContactRepository contactRepository;

    public ConsolidatedContactResponse consolidateContacts(ContactRequest req) {

        String phone = req.getPhoneNumber();
        String email = req.getEmail();

        boolean newInfoRecieved = (phone!=null && !contactRepository.existsByPhoneNumber(phone)) || (email!=null && !contactRepository.existsByEmail(email));
        boolean existingInfoRecieved = (phone!=null && contactRepository.existsByPhoneNumber(phone)) || (email!=null && contactRepository.existsByEmail(email));

        // both phone and email are new
        if (newInfoRecieved && !existingInfoRecieved) {
            // create a primary contact
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

        // if one is new and other is existing
        if (newInfoRecieved) {

            Contact phonePrimaryContact = getPrimaryContactByPhone(req.getPhoneNumber());
            Contact emailPrimaryContact = getPrimaryContactByEmail(req.getEmail());
            Contact primaryContact = phonePrimaryContact != null ? phonePrimaryContact : emailPrimaryContact;

            // create a secondary contact
            Contact contact = new Contact(
                    req.getPhoneNumber(),
                    req.getEmail(),
                    primaryContact.getId(),
                    "secondary",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );
            Contact savedContact = contactRepository.save(contact);
            List<Contact> secondaryContacts = contactRepository.findByLinkedId(primaryContact.getId());
            return new ConsolidatedContactResponse(primaryContact, secondaryContacts);
        }

        // if nothing is new

        // don't create a new contact

        Contact phonePrimaryContact = getPrimaryContactByPhone(req.getPhoneNumber());
        Contact emailPrimaryContact = getPrimaryContactByEmail(req.getEmail());


        // if phone and email each point to different primary contacts
        if(
                phonePrimaryContact != null &&
                        emailPrimaryContact != null &&
                        !phonePrimaryContact.getId().equals(emailPrimaryContact.getId())
        ) {
            Contact primaryContact = phonePrimaryContact.getCreatedAt().isBefore(emailPrimaryContact.getCreatedAt()) ? phonePrimaryContact : emailPrimaryContact;
            Contact otherContact = phonePrimaryContact.getCreatedAt().isBefore(emailPrimaryContact.getCreatedAt()) ? emailPrimaryContact : phonePrimaryContact;

            List<Contact> secondaryOfOther = contactRepository.findByLinkedId(otherContact.getId());

            // make other and its secondary contacts point to primary
            secondaryOfOther.forEach(con -> {
                con.setLinkedId(primaryContact.getId());
                con.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(con);
            });
            otherContact.setLinkedId(primaryContact.getId());
            otherContact.setLinkPrecedence("secondary");
            otherContact.setUpdatedAt(LocalDateTime.now());
            contactRepository.save(otherContact);

            List<Contact> secondaryContacts = contactRepository.findByLinkedId(primaryContact.getId());
            return new ConsolidatedContactResponse(primaryContact, secondaryContacts);
        }
        // if only one primary contact is being pointed to
        else {
            Contact primaryContact = phonePrimaryContact != null ? phonePrimaryContact : emailPrimaryContact;

            List<Contact> secondaryContacts = contactRepository.findByLinkedId(primaryContact.getId());
            return new ConsolidatedContactResponse(primaryContact, secondaryContacts);
        }
    }


    private Contact getPrimaryContactByPhone(String phone) {
        if(phone == null || phone.isBlank()) {
            return null;
        }

        Optional<Contact> oContact = contactRepository.findFirstByPhoneNumber(phone);
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

        Optional<Contact> oContact = contactRepository.findFirstByEmail(email);
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
