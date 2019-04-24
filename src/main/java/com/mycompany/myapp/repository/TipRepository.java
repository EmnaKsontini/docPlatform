package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tip;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Tip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TipRepository extends JpaRepository<Tip, Long>, JpaSpecificationExecutor<Tip> {

    List<Tip> findAll();
}
