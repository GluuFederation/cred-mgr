package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.gluu.credmgr.domain.Gluu;
import org.gluu.credmgr.repository.GluuRepository;
import org.gluu.credmgr.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Gluu.
 */
@RestController
@RequestMapping("/api")
public class GluuResource {

    private final Logger log = LoggerFactory.getLogger(GluuResource.class);
        
    @Inject
    private GluuRepository gluuRepository;
    
    /**
     * POST  /gluus : Create a new gluu.
     *
     * @param gluu the gluu to create
     * @return the ResponseEntity with status 201 (Created) and with body the new gluu, or with status 400 (Bad Request) if the gluu has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/gluus",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gluu> createGluu(@RequestBody Gluu gluu) throws URISyntaxException {
        log.debug("REST request to save Gluu : {}", gluu);
        if (gluu.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("gluu", "idexists", "A new gluu cannot already have an ID")).body(null);
        }
        Gluu result = gluuRepository.save(gluu);
        return ResponseEntity.created(new URI("/api/gluus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("gluu", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /gluus : Updates an existing gluu.
     *
     * @param gluu the gluu to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated gluu,
     * or with status 400 (Bad Request) if the gluu is not valid,
     * or with status 500 (Internal Server Error) if the gluu couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/gluus",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gluu> updateGluu(@RequestBody Gluu gluu) throws URISyntaxException {
        log.debug("REST request to update Gluu : {}", gluu);
        if (gluu.getId() == null) {
            return createGluu(gluu);
        }
        Gluu result = gluuRepository.save(gluu);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("gluu", gluu.getId().toString()))
            .body(result);
    }

    /**
     * GET  /gluus : get all the gluus.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of gluus in body
     */
    @RequestMapping(value = "/gluus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Gluu> getAllGluus() {
        log.debug("REST request to get all Gluus");
        List<Gluu> gluus = gluuRepository.findAll();
        return gluus;
    }

    /**
     * GET  /gluus/:id : get the "id" gluu.
     *
     * @param id the id of the gluu to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the gluu, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/gluus/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gluu> getGluu(@PathVariable Long id) {
        log.debug("REST request to get Gluu : {}", id);
        Gluu gluu = gluuRepository.findOne(id);
        return Optional.ofNullable(gluu)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /gluus/:id : delete the "id" gluu.
     *
     * @param id the id of the gluu to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/gluus/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteGluu(@PathVariable Long id) {
        log.debug("REST request to delete Gluu : {}", id);
        gluuRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gluu", id.toString())).build();
    }

}
