package com.fluxCart.identityReconciliation.payload.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fluxCart.identityReconciliation.model.Contact;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
          Set<String> emailSet = secondaryContacts.stream()
                  .map(Contact::getEmail)
                  .filter(email -> email != null && !Objects.equals(priContact.getEmail(), email))
                  .collect(Collectors.toSet());

          this.emails.addAll(emailSet);


          this.phoneNumbers = new ArrayList<>();
          if(priContact.getPhoneNumber() != null) {
               this.phoneNumbers.add(priContact.getPhoneNumber());
          }
          Set<String> phoneSet = secondaryContacts.stream()
                  .map(Contact::getPhoneNumber)
                  .filter(phoneNumber -> phoneNumber != null &&  !Objects.equals(priContact.getPhoneNumber(), phoneNumber))
                  .collect(Collectors.toSet());
          this.phoneNumbers.addAll(phoneSet);

          this.secondaryContactIds = secondaryContacts.stream()
                  .map(Contact::getId)
                  .toList();
     }
}
