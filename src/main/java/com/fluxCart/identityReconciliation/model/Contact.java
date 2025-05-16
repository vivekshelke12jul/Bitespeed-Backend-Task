package com.fluxCart.identityReconciliation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
* id                   Int
    phoneNumber          String?
    email                String?
    linkedId             Int? // the ID of another Contact linked to this one
    linkPrecedence       "secondary"|"primary" // "primary" if it's the first Contact in the link
    createdAt            DateTime
    updatedAt            DateTime
    deletedAt            DateTime?
*
* */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue
    private Integer id;
    private String phoneNumber;
    private String email;
    private Integer linkedId;
    private String linkPrecedence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
