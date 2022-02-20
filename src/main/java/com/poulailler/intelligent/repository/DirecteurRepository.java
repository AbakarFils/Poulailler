package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Directeur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Directeur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DirecteurRepository extends JpaRepository<Directeur, Long>, JpaSpecificationExecutor<Directeur> {}
