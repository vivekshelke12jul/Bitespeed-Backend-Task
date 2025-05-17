package com.fluxCart.identityReconciliation.payload.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fluxCart.identityReconciliation.model.Contact;
import lombok.Data;

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
}
