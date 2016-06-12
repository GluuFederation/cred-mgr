package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang.StringUtils;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.credmgr.web.rest.dto.SingleValueDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

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
    public ResponseEntity<String> registerAccount(@Valid @RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) throws Exception {
        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);
        mailService.sendOPActivationEmail(opConfig, getBaseUrl(request));
        return new ResponseEntity<>(HttpStatus.OK);
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

    @RequestMapping("/openid/login-uri")
    public ResponseEntity<SingleValueDTO> getLoginUri(HttpServletRequest request, @RequestParam(value = "companyShortName") String companyShortName) {
        return opUserService.getLoginUri(companyShortName, getBaseUrl(request) + "/api/openid/login-redirect")
            .map(location -> new ResponseEntity<SingleValueDTO>(new SingleValueDTO(location), HttpStatus.OK))
            .orElse(new ResponseEntity<SingleValueDTO>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping("/openid/logout-uri")
    public ResponseEntity<SingleValueDTO> getLogoutUri(HttpServletRequest request) {
        return opUserService.getLogoutUri(getBaseUrl(request) + "/api/openid/logout-redirect")
            .map(location -> new ResponseEntity<SingleValueDTO>(new SingleValueDTO(location), HttpStatus.OK))
            .orElse(new ResponseEntity<SingleValueDTO>(HttpStatus.NOT_FOUND));
    }


    @RequestMapping("/openid/login-redirect")
    public void loginRedirectionHandler(HttpServletResponse response, HttpServletRequest request,
                                        @RequestParam(required = false, value = "session_state") String sessionState,
                                        @RequestParam(required = false, value = "scope") String scope,
                                        @RequestParam(required = false, value = "state") String state,
                                        @RequestParam(required = false, value = "code") String code) throws IOException {

        if (opUserService.login(getBaseUrl(request) + "/#/reset-password", code).isPresent())
            response.sendRedirect("/#/reset-password");
        else
            response.sendRedirect("/#/error");
    }

    @RequestMapping("/openid/logout-redirect")
    public void logoutRedirectionHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        opUserService.logout(request, response);
        response.sendRedirect("/#/");
    }

    @RequestMapping(value = "/openid/account",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPUser> getAccount() {
        return opUserService.getPrincipal()
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/openid/change_password",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        return opUserService.changePassword(password)
            .map(opConfig -> new ResponseEntity<>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @RequestMapping(value = "/openid/reset_password/init",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
        return null;
    }

    @RequestMapping(value = "/openid/reset_password/finish",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
        return null;
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) &&
            password.length() >= OPUser.PASSWORD_MIN_LENGTH &&
            password.length() <= OPUser.PASSWORD_MAX_LENGTH);
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
