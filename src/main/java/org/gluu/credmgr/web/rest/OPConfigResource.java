package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OPConfig.
 */
@RestController
@RequestMapping("/api")
public class OPConfigResource {
    //TODO: crud operations also should affect scim user
    private final Logger log = LoggerFactory.getLogger(OPConfigResource.class);

    @Inject
    private OPConfigRepository oPConfigRepository;

    /**
     * POST  /o-p-configs : Create a new oPConfig.
     *
     * @param oPConfig the oPConfig to create
     * @return the ResponseEntity with status 201 (Created) and with body the new oPConfig, or with status 400 (Bad Request) if the oPConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/o-p-configs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> createOPConfig(@Valid @RequestBody OPConfig oPConfig) throws URISyntaxException {
        log.debug("REST request to save OPConfig : {}", oPConfig);
        if (oPConfig.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("oPConfig", "idexists", "A new oPConfig cannot already have an ID")).body(null);
        }
        OPConfig result = oPConfigRepository.save(oPConfig);
        return ResponseEntity.created(new URI("/api/o-p-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("oPConfig", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /o-p-configs : Updates an existing oPConfig.
     *
     * @param oPConfig the oPConfig to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated oPConfig,
     * or with status 400 (Bad Request) if the oPConfig is not valid,
     * or with status 500 (Internal Server Error) if the oPConfig couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/o-p-configs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> updateOPConfig(@Valid @RequestBody OPConfig oPConfig) throws URISyntaxException {
        log.debug("REST request to update OPConfig : {}", oPConfig);
        if (oPConfig.getId() == null) {
            return createOPConfig(oPConfig);
        }
        OPConfig result = oPConfigRepository.save(oPConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("oPConfig", oPConfig.getId().toString()))
            .body(result);
    }

    /**
     * GET  /o-p-configs : get all the oPConfigs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of oPConfigs in body
     */
    @RequestMapping(value = "/o-p-configs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OPConfig> getAllOPConfigs() {
        log.debug("REST request to get all OPConfigs");
        List<OPConfig> oPConfigs = oPConfigRepository.findAll();
        return oPConfigs;
    }

    /**
     * GET  /o-p-configs/:id : get the "id" oPConfig.
     *
     * @param id the id of the oPConfig to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the oPConfig, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/o-p-configs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> getOPConfig(@PathVariable Long id) {
        log.debug("REST request to get OPConfig : {}", id);
        OPConfig oPConfig = oPConfigRepository.findOne(id);
        return Optional.ofNullable(oPConfig)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /o-p-configs/:id : delete the "id" oPConfig.
     *
     * @param id the id of the oPConfig to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/o-p-configs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOPConfig(@PathVariable Long id) {
        log.debug("REST request to delete OPConfig : {}", id);
        oPConfigRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("oPConfig", id.toString())).build();
    }

}
