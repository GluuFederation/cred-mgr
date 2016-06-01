package org.gluu.credmgr.repository;

import org.gluu.credmgr.domain.OpenidServerConfiguration;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OpenidServerConfiguration entity.
 */
@SuppressWarnings("unused")
public interface OpenidServerConfigurationRepository extends JpaRepository<OpenidServerConfiguration,Long> {

}
