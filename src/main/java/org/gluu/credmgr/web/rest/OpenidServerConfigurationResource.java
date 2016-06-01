package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.gluu.credmgr.domain.OpenidServerConfiguration;
import org.gluu.credmgr.repository.OpenidServerConfigurationRepository;
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
 * REST controller for managing OpenidServerConfiguration.
 */
@RestController
@RequestMapping("/api")
public class OpenidServerConfigurationResource {

    private final Logger log = LoggerFactory.getLogger(OpenidServerConfigurationResource.class);

    @Inject
    private OpenidServerConfigurationRepository openidServerConfigurationRepository;

    /**
     * POST  /openid-server-configurations : Create a new openidServerConfiguration.
     *
     * @param openidServerConfiguration the openidServerConfiguration to create
     * @return the ResponseEntity with status 201 (Created) and with body the new openidServerConfiguration, or with status 400 (Bad Request) if the openidServerConfiguration has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/openid-server-configurations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OpenidServerConfiguration> createOpenidServerConfiguration(@Valid @RequestBody OpenidServerConfiguration openidServerConfiguration) throws URISyntaxException {
        log.debug("REST request to save OpenidServerConfiguration : {}", openidServerConfiguration);
        if (openidServerConfiguration.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("openidServerConfiguration", "idexists", "A new openidServerConfiguration cannot already have an ID")).body(null);
        }
        OpenidServerConfiguration result = openidServerConfigurationRepository.save(openidServerConfiguration);
        return ResponseEntity.created(new URI("/api/openid-server-configurations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("openidServerConfiguration", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /openid-server-configurations : Updates an existing openidServerConfiguration.
     *
     * @param openidServerConfiguration the openidServerConfiguration to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated openidServerConfiguration,
     * or with status 400 (Bad Request) if the openidServerConfiguration is not valid,
     * or with status 500 (Internal Server Error) if the openidServerConfiguration couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/openid-server-configurations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OpenidServerConfiguration> updateOpenidServerConfiguration(@Valid @RequestBody OpenidServerConfiguration openidServerConfiguration) throws URISyntaxException {
        log.debug("REST request to update OpenidServerConfiguration : {}", openidServerConfiguration);
        if (openidServerConfiguration.getId() == null) {
            return createOpenidServerConfiguration(openidServerConfiguration);
        }
        OpenidServerConfiguration result = openidServerConfigurationRepository.save(openidServerConfiguration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("openidServerConfiguration", openidServerConfiguration.getId().toString()))
            .body(result);
    }

    /**
     * GET  /openid-server-configurations : get all the openidServerConfigurations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of openidServerConfigurations in body
     */
    @RequestMapping(value = "/openid-server-configurations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OpenidServerConfiguration> getAllOpenidServerConfigurations() {
        log.debug("REST request to get all OpenidServerConfigurations");
        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        return openidServerConfigurations;
    }

    /**
     * GET  /openid-server-configurations/:id : get the "id" openidServerConfiguration.
     *
     * @param id the id of the openidServerConfiguration to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the openidServerConfiguration, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/openid-server-configurations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OpenidServerConfiguration> getOpenidServerConfiguration(@PathVariable Long id) {
        log.debug("REST request to get OpenidServerConfiguration : {}", id);
        OpenidServerConfiguration openidServerConfiguration = openidServerConfigurationRepository.findOne(id);
        return Optional.ofNullable(openidServerConfiguration)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /openid-server-configurations/:id : delete the "id" openidServerConfiguration.
     *
     * @param id the id of the openidServerConfiguration to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/openid-server-configurations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOpenidServerConfiguration(@PathVariable Long id) {
        log.debug("REST request to delete OpenidServerConfiguration : {}", id);
        openidServerConfigurationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("openidServerConfiguration", id.toString())).build();
    }

}
