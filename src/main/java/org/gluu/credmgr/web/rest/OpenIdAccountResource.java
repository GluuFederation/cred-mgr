package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang.StringUtils;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.error.OPException;
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
    public ResponseEntity<String> registerAccount(@Valid @RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) throws OPException {
        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);
        mailService.sendOPActivationEmail(opConfig, getBaseUrl(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/activate",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) throws OPException {
        opUserService.activateOPAdminRegistration(key);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/openid/login-uri")
    public ResponseEntity<SingleValueDTO> getLoginUri(HttpServletRequest request, @RequestParam(value = "companyShortName") String companyShortName) throws OPException {
        String loginUrl = opUserService.getLoginUri(companyShortName, getBaseUrl(request) + "/api/openid/login-redirect");
        return new ResponseEntity<SingleValueDTO>(new SingleValueDTO(loginUrl), HttpStatus.OK);
    }

    @RequestMapping("/openid/logout-uri")
    public ResponseEntity<SingleValueDTO> getLogoutUri(HttpServletRequest request) throws OPException {
        String logoutUrl = opUserService.getLogoutUri(getBaseUrl(request) + "/api/openid/logout-redirect");
        return new ResponseEntity<SingleValueDTO>(new SingleValueDTO(logoutUrl), HttpStatus.OK);
    }


    @RequestMapping("/openid/login-redirect")
    public void loginRedirectionHandler(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "code") String code) throws IOException {
        try {
            opUserService.login(getBaseUrl(request) + "/#/reset-password", code);
            response.sendRedirect("/#/reset-password");
        } catch (OPException e) {
            response.sendRedirect("/#/error");
        }
    }

    @RequestMapping("/openid/logout-redirect")
    public void logoutRedirectionHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        opUserService.logout(request, response);
        response.sendRedirect("/#/");
    }

    @RequestMapping(value = "/openid/account",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    public ResponseEntity<?> changePassword(@RequestBody String password) throws OPException {
        if (!checkPasswordLength(password))
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        opUserService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
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
