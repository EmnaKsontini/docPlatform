package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.domain.Request;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Request entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    @Query("select request from Request request left join fetch request.appointment where request.id =:id")
    Optional<Request> findOneWithEagerRelationships(@Param("id") Long id);

}
