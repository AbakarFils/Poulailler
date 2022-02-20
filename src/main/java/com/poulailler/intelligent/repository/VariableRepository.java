package com.poulailler.intelligent.repository;

import com.poulailler.intelligent.domain.Variable;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Variable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VariableRepository extends JpaRepository<Variable, Long> {
    @Query("select variable from Variable variable where variable.consulter.login = ?#{principal.username}")
    List<Variable> findByConsulterIsCurrentUser();
}
