package com.fluxCart.identityReconciliation.repository;

import com.fluxCart.identityReconciliation.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findByPhoneNumber(String phone);
    Optional<Contact> findByEmail(String email);
    List<Contact> findByLinkedId(Integer id);

}
