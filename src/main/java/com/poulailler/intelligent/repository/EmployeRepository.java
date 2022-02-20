package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Employe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Employe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long>, JpaSpecificationExecutor<Employe> {}
