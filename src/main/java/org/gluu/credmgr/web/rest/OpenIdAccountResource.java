package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by eugeniuparvan on 5/30/16.
 */
@RestController
@RequestMapping("/api")
public class OpenidAccountResource {

    @Inject
    private OPUserService opUserService;

    @Inject
    private MailService mailService;

    @RequestMapping(value = "/openid/register", method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Timed
    public ResponseEntity<?> registerAccount(@Valid @RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) throws JAXBException, IOException, OPException {
        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);
        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);
        mailService.sendOPActivationEmail(opConfig, getBaseUrl(request));
        return null;
    }

    @RequestMapping(value = "/openid/activate",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return opUserService.activateOPAdminRegistration(key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @RequestMapping("/openid/login")
    public void login(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "companyShortName") String companyShortName) throws OPException {
        Optional<String> location = opUserService.getLoginUri(companyShortName, getBaseUrl(request) + "/api/openid/login-redirect");
        if (location.isPresent())
            response.setHeader("Location", location.get());
        else
            throw new OPException(OPException.CAN_NOT_RETRIEVE_LOGIN_URI);
    }

    @RequestMapping("/openid/login-redirect")
    public void loginRedirectionHandler(HttpServletResponse response, HttpServletRequest request,
                                        @RequestParam(required = false, value = "session_state") String sessionState,
                                        @RequestParam(required = false, value = "scope") String scope,
                                        @RequestParam(required = false, value = "state") String state,
                                        @RequestParam(required = false, value = "code") String code) throws IOException {

        response.sendRedirect("/#/reset-password");
    }

    @RequestMapping("/openid/logout-redirect")
    public void logoutRedirectionHandler() {

    }


    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() +
            "://" +
            request.getServerName() +
            ":" +
            request.getServerPort() +
            request.getContextPath();
    }
}
