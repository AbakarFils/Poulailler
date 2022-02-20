package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Humidite;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Humidite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HumiditeRepository extends JpaRepository<Humidite, Long>, JpaSpecificationExecutor<Humidite> {}
