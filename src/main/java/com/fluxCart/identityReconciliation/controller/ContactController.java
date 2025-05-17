package com.fluxCart.identityReconciliation.controller;

import com.fluxCart.identityReconciliation.payload.request.ContactRequest;
import com.fluxCart.identityReconciliation.payload.response.ConsolidatedContactResponse;
import com.fluxCart.identityReconciliation.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/identify")
    public ConsolidatedContactResponse identifyContact(@RequestBody ContactRequest req) {
        return contactService.consolidateContacts(req);
    }
}
