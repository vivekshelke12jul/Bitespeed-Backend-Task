package com.fluxCart.identityReconciliation.service;

import com.fluxCart.identityReconciliation.model.Contact;
import com.fluxCart.identityReconciliation.payload.request.ContactRequest;
import com.fluxCart.identityReconciliation.payload.response.ConsolidatedContactResponse;
import com.fluxCart.identityReconciliation.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
    }

    private Contact getPrimaryContactByPhone(String phone) {
        if(phone == null || phone.isBlank()) {
            return null;
        }

        Optional<Contact> oContact = contactRepository.findByPhoneNumber(phone);
        if(oContact.isEmpty()) {
            return null;
        }

        // if contact with this phone is present
        // find if this contact is primary ,else get the primary contact from this contact
        Contact contact = oContact.get();
        Contact priContactPhone = contact.getLinkPrecedence().equals("primary") ? contact : contactRepository.findByLinkedId(contact.getId()).get();
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
        Contact priContactEmail = contact.getLinkPrecedence().equals("primary") ? contact : contactRepository.findByLinkedId(contact.getId()).get();
        return priContactEmail;
    }


}
