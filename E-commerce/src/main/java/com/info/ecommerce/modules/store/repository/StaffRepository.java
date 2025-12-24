package com.info.ecommerce.modules.store.repository;

import com.info.ecommerce.modules.store.entity.Staff;
import com.info.ecommerce.modules.store.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByAccount(String account);

    boolean existsByAccount(String account);

    List<Staff> findByRole(StaffRole role);

    List<Staff> findByEnabled(Boolean enabled);

    long countByEnabledTrue();
}
