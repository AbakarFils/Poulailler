package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.NH3;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NH3 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NH3Repository extends JpaRepository<NH3, Long>, JpaSpecificationExecutor<NH3> {}
