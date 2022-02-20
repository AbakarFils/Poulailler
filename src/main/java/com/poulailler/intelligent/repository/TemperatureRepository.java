package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Temperature;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Temperature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Long>, JpaSpecificationExecutor<Temperature> {}
