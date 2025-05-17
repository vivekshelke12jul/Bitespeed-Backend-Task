package com.fluxCart.identityReconciliation.payload.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fluxCart.identityReconciliation.model.Contact;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonRootName("contact")
public class ConsolidatedContactResponse {
     private Integer primaryContactId;
     private List<String> emails;
     private List<String> phoneNumbers;
     private List<Integer> secondaryContactIds;

     public ConsolidatedContactResponse(Contact contact) {
          this.primaryContactId = contact.getId();
          this.emails = contact.getEmail() == null ? List.of() : List.of(contact.getEmail());
          this.phoneNumbers = contact.getPhoneNumber() == null ? List.of() : List.of(contact.getPhoneNumber());
          this.secondaryContactIds = List.of();
     }

     public ConsolidatedContactResponse(Contact priContact, List<Contact> secondaryContacts) {
          this.primaryContactId = priContact.getId();

          this.emails = new ArrayList<>();
          if(priContact.getEmail() != null) {
               this.emails.add(priContact.getEmail());
          }
          this.emails.addAll(
                  secondaryContacts.stream()
                          .filter(contact -> contact.getEmail() != null)
                          .map(Contact::getEmail)
                          .toList()
          );

          this.phoneNumbers = new ArrayList<>();
          if(priContact.getPhoneNumber() != null) {
               this.phoneNumbers.add(priContact.getPhoneNumber());
          }
          this.phoneNumbers.addAll(
                  secondaryContacts.stream()
                          .filter(contact -> contact.getPhoneNumber() != null)
                          .map(Contact::getPhoneNumber)
                          .toList()
          );

          this.secondaryContactIds = secondaryContacts.stream()
                  .map(Contact::getId)
                  .toList();
     }
}
