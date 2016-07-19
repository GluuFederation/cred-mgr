package org.gluu.credmgr.service;

import gluu.scim2.client.Scim2Client;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.service.util.RandomUtil;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.oxtrust.model.scim2.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(rollbackFor = Exception.class)
public class OPUserService {

    @Value("${credmgr.gluuIdpOrg.companyShortName}")
    private String companyShortName;

    @Value("${credmgr.gluuIdpOrg.requiredOPSuperAdminClaimValue}")
    private String opSuperAdmin;

    @Inject
    private ScimService scimService;

    @Inject
    private OxauthService oxauthService;

    @Inject
    private OPConfigRepository opConfigRepository;

    private final ObjectMapper objectMapper;

    public OPUserService() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Registering new user with OP_ADMIN role
     *
     * @param registrationDTO
     * @return
     * @throws OPException: OPException.ERROR_CREATE_SCIM_USER
     */
    public OPConfig createOPAdminInformation(RegistrationDTO registrationDTO) throws OPException {
        OPConfig defaultConfig = getDefaultConfig().orElseThrow(() -> new OPException(OPException.ERROR_CREATE_SCIM_USER));

        User user = new User();
        user.setUserName(registrationDTO.getCompanyShortName());
        user.setPassword(registrationDTO.getPassword());
        user.setDisplayName(registrationDTO.getCompanyName());
        user.setNickName("");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("");

        Name name = new Name();
        name.setGivenName(registrationDTO.getFirstName());
        name.setFamilyName(registrationDTO.getLastName());
        user.setName(name);

        Email email = new Email();
        email.setType(Email.Type.WORK);
        email.setPrimary(true);
        email.setValue(registrationDTO.getEmail());
        email.setDisplay(registrationDTO.getEmail());
        email.setOperation("CREATE");
        email.setReference("");
        user.setEmails(Arrays.asList(new Email[]{email}));

        try {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            extensionBuilder.setField(defaultConfig.getRequiredClaim(), defaultConfig.getRequiredClaimValue());
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        }
        //TODO: setActive(false);
        user.setActive(false);

        user = scimService.createPerson(user);

        OPConfig opConfig = new OPConfig();
        opConfig.setAdminScimId(user.getId());
        opConfig.setActivated(false);
        opConfig.setEmail(registrationDTO.getEmail());
        opConfig.setActivationKey(RandomUtil.generateActivationKey());
        opConfig.setCompanyName(registrationDTO.getCompanyName());
        opConfig.setCompanyShortName(registrationDTO.getCompanyShortName());
        return opConfigRepository.save(opConfig);
    }

    /**
     * @param key
     * @throws OPException: OPException.ERROR_ACTIVATE_OP_ADMIN)
     */
    public void activateOPAdminRegistration(String key) throws OPException {
        opConfigRepository.findOneByActivationKey(key).map(opConfig -> {
            try {
                User user = scimService.retrievePerson(opConfig.getAdminScimId());
                user.setActive(true);
                user.setPassword(null);
                scimService.updatePerson(user, user.getId());

                opConfig.setActivated(true);
                opConfig.setActivationKey(null);
                opConfigRepository.save(opConfig);
                return opConfig;
            } catch (OPException e) {
                return null;
            }
        }).orElseThrow(() -> new OPException(OPException.ERROR_ACTIVATE_OP_ADMIN));
    }

    /**
     * @param companyShortName
     * @param redirectUri
     * @return
     * @throws OPException: OPException.ERROR_RETRIEVE_LOGIN_URI
     *                      OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION
     *                      OPException.ERROR_RETRIEVE_OP_CONFIG
     */
    public String getLoginUri(String companyShortName, String redirectUri) throws OPException {
        OPConfig opConfig = opConfigRepository.findOneByCompanyShortName(companyShortName).orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_OP_CONFIG));

        String host = opConfig.getHost();
        String clientId = opConfig.getClientId();
        List<ResponseType> responseTypes = Arrays.asList(new ResponseType[]{ResponseType.CODE});
        List<String> scopes = Arrays.asList(new String[]{"openid", opConfig.getRequiredOpenIdScope()});

        OPUser opUser = new OPUser();
        opUser.getAuthorities().add(OPAuthority.OP_ANONYMOUS);
        opUser.setHost(host);
        opUser.setLoginOpConfigId(opConfig.getId());

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

            Optional<OPConfig> opConfig = opUser.map(user -> opConfigRepository.findOne(opUser.get().getLoginOpConfigId()));
            OPConfig config = opConfig.orElseThrow(() -> new OPException(OPException.ERROR_LOGIN));
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
            if (opSuperAdmin.equals(scimRole)) {
                authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_SUPER_ADMIN.toString()));
                user.getAuthorities().add(OPAuthority.OP_SUPER_ADMIN);
            } else if (config.getRequiredClaimValue() != null && config.getRequiredClaimValue().equals(scimRole)) {
                authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_ADMIN.toString()));
                user.getAuthorities().add(OPAuthority.OP_ADMIN);

                //Setting admin opConfigId
                Optional<OPConfig> opAdminConfig = Optional.ofNullable(scimUser.getEmails())
                    .map(emails -> {
                        if (emails.size() > 0)
                            return emails.get(0);
                        else
                            return null;
                    })
                    .map(email -> opConfigRepository.findOneByEmail(email.getValue()).orElse(null));
                OPConfig adminConfig = opAdminConfig.orElseThrow(() -> new OPException(OPException.ERROR_LOGIN));
                user.setOpConfigId(adminConfig.getId());
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
    public User requestPasswordReset(ResetPasswordDTO resetPasswordDTO) throws OPException {
        OPConfig opConfig = opConfigRepository.findOneByCompanyShortName(resetPasswordDTO.getCompanyShortName()).filter(OPConfig::isActivated).orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_OP_CONFIG));
        Scim2Client scimClient = scimService.getScimClient(opConfig.getHost(), opConfig.getUmaAatClientId(), opConfig.getClientJWKS(), opConfig.getUmaAatClientKeyId());
        User user = scimService.searchUsers("mail eq \"" + resetPasswordDTO.getEmail() + "\"", scimClient).stream().findFirst().orElseThrow(() -> new OPException(OPException.ERROR_FIND_SCIM_USER));
        user.addExtension(Optional.of(user.getExtensions())
            .map(extensions -> extensions.get(Constants.USER_EXT_SCHEMA_ID))
            .map(extension -> new Extension.Builder(extension))
            .map(eBuilder -> eBuilder.setField("resetKey", RandomUtil.generateResetKey()).setField("resetDate", ZonedDateTime.now().toString()).build())
            .orElse(null));
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
        OPConfig opConfig = opConfigRepository.findOneByCompanyShortName(keyAndPasswordDTO.getCompanyShortName()).filter(OPConfig::isActivated).orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_OP_CONFIG));
        Scim2Client scimClient = scimService.getScimClient(opConfig.getHost(), opConfig.getUmaAatClientId(), opConfig.getClientJWKS(), opConfig.getUmaAatClientKeyId());
        User scimUser = scimService.searchUsers("resetKey eq \"" + keyAndPasswordDTO.getKey() + "\"", scimClient).stream()
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                String resetDate = user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING);
                return ZonedDateTime.parse(resetDate).isAfter(oneDayAgo);
            }).findFirst().get();
        scimUser.setPassword(keyAndPasswordDTO.getNewPassword());
        scimUser.addExtension(new Extension.Builder(scimUser.getExtension(Constants.USER_EXT_SCHEMA_ID)).setField("resetKey", "empty").setField("resetDate", "empty").build());
        scimService.updatePerson(scimUser, scimUser.getId());
    }

    public Optional<OPUser> getPrincipal() {
        return
            Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> authentication.getPrincipal())
                .filter(OPUser.class::isInstance)
                .map(OPUser.class::cast)
                .map(opUser -> {
                    if (StringUtils.isNotEmpty(opUser.getLogin())) {
                        OPUser clonedOpUser = SerializationUtils.clone(opUser);
                        clonedOpUser.setOpConfig(opConfigRepository.findOne(clonedOpUser.getLoginOpConfigId()));
                        return clonedOpUser;
                    } else {
                        return null;
                    }
                });
    }

    public Optional<OPConfig> getAdminOpConfig(OPUser user) {
        return Optional.ofNullable(opConfigRepository.findOne(user.getOpConfigId()));
    }

    private Optional<OPConfig> getDefaultConfig() {
        return opConfigRepository.findOneByCompanyShortName(companyShortName);
    }
}
