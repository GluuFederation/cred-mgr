package org.gluu.credmgr.service;

import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.service.util.RandomUtil;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;
import org.xdi.oxauth.model.common.AuthorizationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by eugeniuparvan on 6/3/16.
 */
@Service
public class OPUserService {

    //TODO: HIGH. Why scim returns empty phone numbers list?
    //TODO: scheduler to delete op admins that are not presented in db

    //TODO: MEDIUM. reset-password error handling when jks path is invalid
    //TODO: MEDIUM. tests
    //TODO: MEDIUM. add unit tests for new java methods
    //TODO: MEDIUM. add new cases in OPConfigResourceIntTest with wrong patterns

    //TODO: LOW. Auto logout when gluu server session is ended.
    //TODO: LOW. after server restarts intercept end of session
    //TODO: LOW. change prefix on jks file to random generated number
    //TODO: LOW. reset.password.html phone number twilio validation(serverside)

    @Inject
    private ScimService scimService;

    @Inject
    private OxauthService oxauthService;

    @Inject
    private OPConfigRepository opConfigRepository;


    /**
     * @param redirectUri
     * @return
     * @throws OPException: OPException.ERROR_RETRIEVE_LOGIN_URI
     *                      OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION
     *                      OPException.ERROR_RETRIEVE_OP_CONFIG
     */
    public String getLoginUri(String redirectUri) throws OPException {
        OPConfig opConfig = opConfigRepository.get();

        String host = opConfig.getHost();
        String clientId = opConfig.getClientId();
        List<ResponseType> responseTypes = Arrays.asList(new ResponseType[]{ResponseType.CODE});
        List<String> scopes = Arrays.asList(new String[]{"openid", opConfig.getRequiredOpenIdScope()});

        OPUser opUser = new OPUser();
        opUser.getAuthorities().add(OPAuthority.OP_ANONYMOUS);
        opUser.setHost(host);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(opUser, null, opUser.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList())));

        String loginUri = oxauthService.getAuthorizationUri(host, clientId, responseTypes, scopes, redirectUri);

        return loginUri;
    }

    /**
     * @param redirectUri
     * @return
     * @throws OPException: OPException.ERROR_RETRIEVE_LOGOUT_URI
     *                      OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION
     */
    public String getLogoutUri(String redirectUri) throws OPException {
        Optional<OPUser> opUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> authentication.getPrincipal())
            .filter(OPUser.class::isInstance)
            .map(OPUser.class::cast);
        OPUser user = opUser.orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_LOGOUT_URI));
        String logoutUri = oxauthService.getLogoutUri(user.getHost(), user.getIdToken(), redirectUri);
        return logoutUri;

    }

    /**
     * @param redirectUri
     * @param code
     * @return
     * @throws OPException: OPException.ERROR_LOGIN
     *                      OPException.ERROR_RETRIEVE_TOKEN
     *                      OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION
     *                      OPException.ERROR_RETRIEVE_USER_INFO
     *                      OPException.ERROR_FIND_SCIM_USER
     */
    public OPUser login(String redirectUri, String code, HttpServletRequest request, HttpServletResponse response) throws OPException {
        try {
            Optional<OPUser> opUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> authentication.getPrincipal())
                .filter(OPUser.class::isInstance)
                .map(OPUser.class::cast);

            OPConfig config = opConfigRepository.get();
            OPUser user = opUser.get();

            String host = config.getHost();
            GrantType grantType = GrantType.AUTHORIZATION_CODE;
            String clientId = config.getClientId();
            String clientSecret = config.getClientSecret();
            String requiredScope = config.getRequiredOpenIdScope();

            TokenResponse tokenResponse = oxauthService.getToken(host, grantType, clientId, clientSecret, code, redirectUri, "openid" + " " + requiredScope);
            UserInfoResponse userInfoResponse = oxauthService.getUserInfo(host, tokenResponse.getAccessToken(), AuthorizationMethod.AUTHORIZATION_REQUEST_HEADER_FIELD);

            List<String> claimList = userInfoResponse.getClaim("inum");
            if (claimList == null || claimList.size() == 0)
                throw new OPException(OPException.ERROR_LOGIN);

            User scimUser = scimService.retrievePerson(claimList.get(0));
            String scimRole;
            try {
                scimRole = Optional.of(scimUser.getExtensions())
                    .map(extensions -> extensions.get(Constants.USER_EXT_SCHEMA_ID))
                    .map(extension -> extension.getField(config.getRequiredClaim(), ExtensionFieldType.STRING))
                    .orElse(null);
            } catch (Exception e) {
                scimRole = null;
            }

            //Setting authorities
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getAuthorities().remove(OPAuthority.OP_ANONYMOUS);
            if (config.getRequiredClaimValue() != null && config.getRequiredClaimValue().equals(scimRole)) {
                authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_ADMIN.toString()));
                user.getAuthorities().add(OPAuthority.OP_ADMIN);
            } else {
                authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_USER.toString()));
                user.getAuthorities().add(OPAuthority.OP_USER);
            }

            user.setScimId(scimUser.getId());
            user.setLogin(scimUser.getUserName());
            user.setLangKey(scimUser.getLocale());
            user.setIdToken(tokenResponse.getIdToken());

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities().stream().map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList())));

            return user;
        } catch (Exception e) {
            logout(request, response);
            throw e;
        }
    }

    /**
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && request != null && response != null)
            new SecurityContextLogoutHandler().logout(request, response, auth);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * @param password
     * @return
     * @throws OPException: OPException.ERROR_PASSWORD_CHANGE
     *                      OPException.ERROR_FIND_SCIM_USER
     *                      OPException.ERROR_UPDATE_SCIM_USER
     */
    public OPUser changePassword(String password) throws OPException {
        Optional<OPUser> opUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> authentication.getPrincipal())
            .filter(OPUser.class::isInstance)
            .map(OPUser.class::cast);
        OPUser user = opUser.orElseThrow(() -> new OPException(OPException.ERROR_PASSWORD_CHANGE));
        User scimUser = scimService.retrievePerson(user.getScimId());
        scimUser.setPassword(password);
        scimService.updatePerson(scimUser, scimUser.getId());
        return user;
    }

    /**
     * @param resetPasswordDTO
     * @return
     * @throws OPException: OPException.ERROR_RETRIEVE_OP_CONFIG
     *                      OPException.ERROR_FIND_SCIM_USER
     *                      OPException.ERROR_UPDATE_SCIM_USER
     */
    public User requestPasswordResetWithEmail(ResetPasswordDTO resetPasswordDTO) throws OPException {
        User user = scimService.searchUsers("mail eq \"" + resetPasswordDTO.getEmail() + "\"").stream().findFirst().orElseThrow(() -> new OPException(OPException.ERROR_FIND_SCIM_USER));
        addUserExtensionIfNotExist(user);
        user.addExtension(Optional.of(user.getExtensions())
            .map(extensions -> extensions.get(Constants.USER_EXT_SCHEMA_ID))
            .map(extension -> new Extension.Builder(extension))
            .map(eBuilder -> eBuilder.setField("resetKey", RandomUtil.generateResetKey()).setField("resetDate", ZonedDateTime.now().toString()).build())
            .orElse(null));

        user.setPassword(null);
        user = scimService.updatePerson(user, user.getId());
        return user;
    }

    /**
     * @param resetPasswordDTO
     * @return
     * @throws OPException: OPException.ERROR_RETRIEVE_OP_CONFIG
     *                      OPException.ERROR_FIND_SCIM_USER
     *                      OPException.ERROR_UPDATE_SCIM_USER
     */
    public User requestPasswordResetWithMobile(ResetPasswordDTO resetPasswordDTO) throws OPException {
        User user = scimService.searchUsers("resetPhoneNumber eq \"" + resetPasswordDTO.getMobile() + "\"").stream().findFirst().orElseThrow(() -> new OPException(OPException.ERROR_FIND_SCIM_USER));
        addUserExtensionIfNotExist(user);
        user.addExtension(Optional.of(user.getExtensions())
            .map(extensions -> extensions.get(Constants.USER_EXT_SCHEMA_ID))
            .map(extension -> new Extension.Builder(extension))
            .map(eBuilder -> eBuilder.setField("resetKey", RandomUtil.generateResetKey()).setField("resetDate", ZonedDateTime.now().toString()).build())
            .orElse(null));

        user.setPassword(null);
        user = scimService.updatePerson(user, user.getId());
        return user;
    }

    /**
     * @param keyAndPasswordDTO
     * @throws OPException: OPException.ERROR_FIND_SCIM_USER
     *                      OPException.ERROR_UPDATE_SCIM_USER
     *                      OPException.ERROR_RETRIEVE_OP_CONFIG
     */
    public void completePasswordReset(KeyAndPasswordDTO keyAndPasswordDTO) throws OPException {
        User scimUser = scimService.searchUsers("resetKey eq \"" + keyAndPasswordDTO.getKey() + "\"").stream()
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                String resetDate = user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING);
                return ZonedDateTime.parse(resetDate).isAfter(oneDayAgo);
            }).findFirst().orElseThrow(() -> new OPException(OPException.ERROR_FIND_SCIM_USER));
        scimUser.setPassword(keyAndPasswordDTO.getNewPassword());
        scimUser.addExtension(new Extension.Builder(scimUser.getExtension(Constants.USER_EXT_SCHEMA_ID)).setField("resetKey", "empty").setField("resetDate", "empty").build());
        scimService.updatePerson(scimUser, scimUser.getId());
    }

    public Optional<OPUser> getPrincipal() {
        return
            Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> authentication.getPrincipal())
                .filter(OPUser.class::isInstance)
                .map(OPUser.class::cast);
    }

    public void unregisterFido() throws OPException {
        OPUser principal = getPrincipal().orElseThrow(() -> new OPException(OPException.ERROR_DELETE_FIDO_DEVICE));
        scimService.unregisterFido(principal.getScimId());
    }


    private void addUserExtensionIfNotExist(User user) {
        if (user.getExtensions().size() == 0) {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            user.addExtension(extensionBuilder.build());
        }
    }
}
