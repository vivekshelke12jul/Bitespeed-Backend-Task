package com.fluxCart.identityReconciliation.payload.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.List;

@Data
@JsonRootName("contact")
public class ConsolidatedContactResponse {
     private Integer primaryContactId;
     private List<String> emails;
     private List<String> phoneNumbers;
     private List<Integer> secondaryContactIds;
}
