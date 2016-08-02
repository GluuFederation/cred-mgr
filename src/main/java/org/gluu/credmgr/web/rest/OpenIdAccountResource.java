package org.gluu.credmgr.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang.StringUtils;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.credmgr.web.rest.dto.SingleValueDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by eugeniuparvan on 5/30/16.
 */
@RestController
@RequestMapping("/api")
public class OpenidAccountResource implements ResourceLoaderAware {

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private OPUserService opUserService;

    @Inject
    private MailService mailService;

    @Inject
    private OPConfigResource opConfigResource;

    private ResourceLoader resourceLoader;

    @RequestMapping(value = "/openid/settings-update",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OPConfig> updateSettings(@Valid final OPConfig opConfig, @RequestParam(value = "file", required = false) final MultipartFile file) throws OPException {
        if ((file == null || file.isEmpty()) && opConfig.getClientJKS() == null)
            throw new OPException(OPException.ERROR_UPDATE_OP_CONFIG);

        ResponseEntity<OPConfig> responseEntity;
        try {
            if (file != null && !file.isEmpty()) {
                File path = new File(credmgrProperties.getJksStorePath() + "/" + opConfig.getCompanyShortName());
                if (!path.exists())
                    path.mkdir();
                Files.copy(file.getInputStream(), Paths.get(path.getAbsolutePath(), file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                opConfig.setClientJKS("/" + opConfig.getCompanyShortName() + "/" + file.getOriginalFilename());
            }
            responseEntity = opConfigResource.updateOPConfig(opConfig);
        } catch (URISyntaxException | IOException | RuntimeException e) {
            throw new OPException(OPException.ERROR_UPDATE_OP_CONFIG);
        }

        //Test
        Resource resource = resourceLoader.getResource("file:" + Paths.get(credmgrProperties.getJksStorePath() + "/" + opConfig.getCompanyShortName(), opConfig.getClientJKS()).toString());
        try {
            InputStream is = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseEntity;
    }

    @RequestMapping(value = "/openid/register", method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Timed
    public ResponseEntity<String> registerAccount(@Valid @RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) throws OPException {
        try {
            OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);
            mailService.sendOPActivationEmail(opConfig, getBaseUrl(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS);
        }
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
            OPUser user = opUserService.login(getBaseUrl(request) + "/#/reset-password/", code, request, response);
            if (user.getAuthorities().contains(OPAuthority.OP_ADMIN)) {
                OPConfig adminOpConfig = opUserService.getAdminOpConfig(user).orElseThrow(() -> new OPException(OPException.ERROR_LOGIN));
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
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Timed
    public ResponseEntity<String> requestPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) throws OPException {
        User user = opUserService.requestPasswordReset(resetPasswordDTO);
        mailService.sendPasswordResetMail(user, getBaseUrl(request), resetPasswordDTO.getCompanyShortName());
        return new ResponseEntity<String>("e-mail was sent", HttpStatus.OK);
    }

    @RequestMapping(value = "/openid/reset_password/finish",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
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
