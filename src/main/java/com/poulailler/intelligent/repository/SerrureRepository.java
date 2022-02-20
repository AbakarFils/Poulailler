package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Serrure;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Serrure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SerrureRepository extends JpaRepository<Serrure, Long>, JpaSpecificationExecutor<Serrure> {}
