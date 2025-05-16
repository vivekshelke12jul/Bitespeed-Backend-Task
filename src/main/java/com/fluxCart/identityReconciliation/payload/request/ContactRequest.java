package com.fluxCart.identityReconciliation.payload.request;

import lombok.Data;

@Data
public class ContactRequest {
    private String email;
    private String phoneNumber;
}
