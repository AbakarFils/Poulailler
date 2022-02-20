package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Oeuf;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Oeuf entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OeufRepository extends JpaRepository<Oeuf, Long>, JpaSpecificationExecutor<Oeuf> {}
