package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, String> {
    List<ContractEntity> findByCompanyId(String companyId);
    List<ContractEntity> findByStatus(String status);
}
