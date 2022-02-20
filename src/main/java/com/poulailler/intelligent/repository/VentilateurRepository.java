package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Ventilateur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ventilateur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VentilateurRepository extends JpaRepository<Ventilateur, Long>, JpaSpecificationExecutor<Ventilateur> {}
