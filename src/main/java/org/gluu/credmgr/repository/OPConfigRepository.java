package org.gluu.credmgr.repository;

import org.gluu.credmgr.domain.OPConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the OPConfig entity.
 */
public interface OPConfigRepository extends JpaRepository<OPConfig, Long> {
    Optional<OPConfig> findOneByActivationKey(String activationKey);

    Optional<OPConfig> findOneByCompanyShortName(String companyShortName);

    Optional<OPConfig> findOneByEmail(String email);
}
