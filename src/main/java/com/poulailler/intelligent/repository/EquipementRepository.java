package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Equipement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Equipement entity.
 */
@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Long>, JpaSpecificationExecutor<Equipement> {
    @Query(
        value = "select distinct equipement from Equipement equipement left join fetch equipement.employes left join fetch equipement.gestionnaires",
        countQuery = "select count(distinct equipement) from Equipement equipement"
    )
    Page<Equipement> findAllWithEagerRelationships(Pageable pageable);

    @Query(
        "select distinct equipement from Equipement equipement left join fetch equipement.employes left join fetch equipement.gestionnaires"
    )
    List<Equipement> findAllWithEagerRelationships();

    @Query(
        "select equipement from Equipement equipement left join fetch equipement.employes left join fetch equipement.gestionnaires where equipement.id =:id"
    )
    Optional<Equipement> findOneWithEagerRelationships(@Param("id") Long id);
}
