package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Lampe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lampe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LampeRepository extends JpaRepository<Lampe, Long>, JpaSpecificationExecutor<Lampe> {}
