package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang.StringUtils;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.MobileService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.ResetOptionsDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.credmgr.web.rest.dto.SingleValueDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by eugeniuparvan on 5/30/16.
 */
@RestController
@RequestMapping("/api")
public class OpenidAccountResource implements ResourceLoaderAware {

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private ScimService scimService;

    @Inject
    private OPUserService opUserService;

    @Inject
    private MailService mailService;

    @Inject
    private MobileService mobileService;

//    @Inject
//    private OPConfigResource opConfigResource;

    @Inject
    private OPConfigRepository opConfigRepository;

    private ResourceLoader resourceLoader;

    @RequestMapping(value = "/openid/settings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> updateSettings(@RequestBody final OPConfig opConfig) throws OPException {
        opConfigRepository.save(opConfig);
        return new ResponseEntity<OPConfig>(opConfigRepository.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/settings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> getSettings() throws OPException {
        return new ResponseEntity<OPConfig>(opConfigRepository.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/reset/options",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResetOptionsDTO> getResetOptions() throws OPException {
        OPConfig opConfig = opConfigRepository.get();
        ResetOptionsDTO resetOptionsDTO = new ResetOptionsDTO();
        resetOptionsDTO.setEmail(opConfig.isEnableEmailManagement());
        resetOptionsDTO.setMobile(opConfig.isEnableMobileManagement());
        return new ResponseEntity<ResetOptionsDTO>(resetOptionsDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/login-uri", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleValueDTO> getLoginUri(HttpServletRequest request) throws OPException {
        String loginUrl = opUserService.getLoginUri(getBaseUrl(request) + "/api/openid/login-redirect");
        return new ResponseEntity<SingleValueDTO>(new SingleValueDTO(loginUrl), HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/logout-uri", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleValueDTO> getLogoutUri(HttpServletRequest request) throws OPException {
        String logoutUrl = opUserService.getLogoutUri(getBaseUrl(request) + "/api/openid/logout-redirect");
        return new ResponseEntity<SingleValueDTO>(new SingleValueDTO(logoutUrl), HttpStatus.OK);
    }


    @RequestMapping("/openid/login-redirect")
    public void loginRedirectionHandler(HttpServletResponse response, HttpServletRequest request,
                                        @RequestParam(name = "session_state", required = false) String sessionState,
                                        @RequestParam(value = "code") String code) throws IOException {
        try {
            OPUser user = opUserService.login(getBaseUrl(request) + "/#/reset-password/", sessionState, code, request, response);
            if (user.getAuthorities().contains(OPAuthority.OP_ADMIN)) {
                OPConfig adminOpConfig = opConfigRepository.get();
                if (StringUtils.isEmpty(adminOpConfig.getClientJKS()))
                    response.sendRedirect("/#/settings");
                else
                    response.sendRedirect("/#/reset-password/");
            } else {
                response.sendRedirect("/#/reset-password/");
            }
        } catch (OPException e) {
            response.sendRedirect("/#/error?detailMessage=" + e.getMessage());
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
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) throws OPException {
        if (!checkPasswordLength(password))
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        opUserService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/fido/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<?> unregisterFIDO(@PathVariable String id) throws OPException {
        scimService.unregisterFido(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/fido",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<List<FidoDevice>> getAllFIDO() throws OPException {
        return new ResponseEntity<>(opUserService.getAllFidoDevices(), HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/fido",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<?> updateFIDO(@RequestBody FidoDevice fidoDevice) throws OPException {
        scimService.updateFido(fidoDevice);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/fido",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<List<FidoDevice>> retisterFido(@RequestBody FidoDevice fidoDevice) throws OPException {
        return new ResponseEntity<>(opUserService.getAllFidoDevices(), HttpStatus.OK);
    }


    @RequestMapping(value = "/openid/reset_password/init",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<String> requestPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) throws OPException {
        OPConfig opConfig = opConfigRepository.get();
        User user;
        String baseUrl = getBaseUrl(request);
        if (resetPasswordDTO.getEmail() != null) {
            user = opUserService.requestPasswordResetWithEmail(resetPasswordDTO);
            mailService.sendPasswordResetMail(user, baseUrl, opConfig);
        } else if (resetPasswordDTO.getMobile() != null) {
            user = opUserService.requestPasswordResetWithMobile(resetPasswordDTO);
            mobileService.sendPasswordResetSMS(user, baseUrl, opConfig);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/reset_password/finish",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) throws OPException {
        if (!checkPasswordLength(keyAndPassword.getNewPassword()))
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        opUserService.completePasswordReset(keyAndPassword);
        return new ResponseEntity<>(HttpStatus.OK);
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

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
