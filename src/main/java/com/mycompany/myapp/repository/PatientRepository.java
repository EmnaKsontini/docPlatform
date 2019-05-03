package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Patient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    @Query(value = "select distinct patient from Patient patient left join fetch patient.doctors",
        countQuery = "select count(distinct patient) from Patient patient")
    Page<Patient> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct patient from Patient patient left join fetch patient.doctors")
    List<Patient> findAllWithEagerRelationships();

    @Query("select patient from Patient patient left join fetch patient.doctors where patient.id =:id")
    Optional<Patient> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Patient> findOneByCin(Long l);
}
