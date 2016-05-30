package org.gluu.credmgr.repository;

import org.gluu.credmgr.domain.Gluu;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Gluu entity.
 */
@SuppressWarnings("unused")
public interface GluuRepository extends JpaRepository<Gluu,Long> {

}
