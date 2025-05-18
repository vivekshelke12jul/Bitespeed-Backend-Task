package com.fluxCart.identityReconciliation.repository;

import com.fluxCart.identityReconciliation.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findByPhoneNumberAndEmail(String phone, String email);
    Optional<Contact> findFirstByPhoneNumber(String phone);
    Optional<Contact> findFirstByEmail(String email);
    boolean existsByPhoneNumber(String phone);
    boolean existsByEmail(String email);

    List<Contact> findByLinkedId(Integer id);

}
