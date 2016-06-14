package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.service.util.RandomUtil;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.Role;
import org.gluu.oxtrust.model.scim2.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
import javax.xml.bind.JAXBException;
import java.io.IOException;
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

    @Value("${credmgr.gluuIdpOrg.requiredOPAdminClaimValue}")
    private String opAdmin;

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

    public OPConfig createOPAdminInformation(RegistrationDTO registrationDTO) throws OPException {
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

        Role role = new Role();
        role.setDisplay(opAdmin);
        role.setValue(opAdmin);
        role.setType(new Role.Type(opAdmin));
        role.setPrimary(true);
        role.setOperation("CREATE");
        role.setReference("");
        user.setRoles(Arrays.asList(new Role[]{role}));

        Email email = new Email();
        email.setType(Email.Type.WORK);
        email.setPrimary(true);
        email.setValue(registrationDTO.getEmail());
        email.setDisplay(registrationDTO.getEmail());
        email.setOperation("CREATE");
        email.setReference("");
        user.setEmails(Arrays.asList(new Email[]{email}));

        user.setActive(true);

        ScimResponse scimResponse;
        try {
            scimResponse = scimService.createPerson(user);
            if (scimResponse.getStatusCode() != 201) {
                throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
            }
        } catch (IOException | JAXBException e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER, e);
        }
        try {
            user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER, e);
        }

        OPConfig opConfig = new OPConfig();
        opConfig.setAdminScimId(user.getId());
        opConfig.setActivated(false);
        opConfig.setEmail(registrationDTO.getEmail());
        opConfig.setActivationKey(RandomUtil.generateActivationKey());
        opConfig.setCompanyName(registrationDTO.getCompanyName());
        opConfig.setCompanyShortName(registrationDTO.getCompanyShortName());
        try {
            opConfigRepository.save(opConfig);
        } catch (DataIntegrityViolationException e) {
            throw new OPException(OPException.ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS, e);
        }

        return opConfig;
    }

    public void activateOPAdminRegistration(String key) throws OPException {
        Optional<OPConfig> config = opConfigRepository.findOneByActivationKey(key).map(opConfig -> {
            try {
                ScimResponse scimResponse = scimService.retrievePerson(opConfig.getAdminScimId());
                if (scimResponse.getStatusCode() == 200) {
                    User user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
                    user.setNickName("activated");
                    user.setActive(true);
                    scimResponse = scimService.updatePerson(user, user.getId());
                    if (scimResponse.getStatusCode() != 200)
                        return null;

                    opConfig.setActivated(true);
                    opConfig.setActivationKey(null);
                    opConfigRepository.save(opConfig);
                    return opConfig;
                }
            } catch (IOException | JAXBException e) {
                return null;
            }
            return null;
        });
        if (!config.isPresent()) {
            throw new OPException(OPException.ERROR_ACTIVATE_OP_ADMIN);
        }
    }

    public String getLoginUri(String companyShortName, String redirectUri) throws OPException {
        OPConfig opConfig = opConfigRepository.findOneByCompanyShortName(companyShortName).orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_LOGIN_URI));

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

    public String getLogoutUri(String redirectUri) throws OPException {
        Optional<OPUser> opUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> authentication.getPrincipal())
            .filter(OPUser.class::isInstance)
            .map(OPUser.class::cast);
        OPUser user = opUser.orElseThrow(() -> new OPException(OPException.ERROR_RETRIEVE_LOGOUT_URI));
        String logoutUri = oxauthService.getLogoutUri(user.getHost(), user.getIdToken(), redirectUri);
        return logoutUri;

    }

    public OPUser login(String redirectUri, String code) throws OPException {
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
        if (claimList == null || claimList.size() == 0) {
            throw new OPException(OPException.ERROR_LOGIN);
        }

        try {
            ScimResponse scimResponse = scimService.retrievePerson(claimList.get(0));
            if (scimResponse.getStatusCode() != 200) {
                throw new OPException(OPException.ERROR_LOGIN);
            }
            User scimUser = null;
            try {
                scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
            } catch (IOException e) {
                throw new OPException(OPException.ERROR_LOGIN, e);
            }

            //Setting admin opConfigId
            Optional<OPConfig> opAdminConfig = Optional.ofNullable(scimUser.getEmails())
                .map(emails -> {
                    if (emails.size() > 0)
                        return emails.get(0);
                    else
                        return null;
                })
                .map(email -> opConfigRepository.findOneByEmail(email.getValue()).orElse(null));
            user.setOpConfigId(opAdminConfig.orElseThrow(() -> new OPException(OPException.ERROR_LOGIN)).getId());

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_USER.toString()));
            user.getAuthorities().add(OPAuthority.OP_USER);

            if (scimUser.getRoles() != null) {
                if (scimUser.getRoles().stream().filter(role -> role.getValue().equals(opAdmin)).findFirst()
                    .isPresent()) {
                    authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_ADMIN.toString()));
                    user.getAuthorities().add(OPAuthority.OP_ADMIN);
                }
                if (scimUser.getRoles().stream().filter(role -> role.getValue().equals(opSuperAdmin)).findFirst()
                    .isPresent()) {
                    authorities.add(new SimpleGrantedAuthority(OPAuthority.OP_SUPER_ADMIN.toString()));
                    user.getAuthorities().add(OPAuthority.OP_SUPER_ADMIN);
                }
            }
            user.setScimId(scimUser.getId());
            user.setLogin(scimUser.getUserName());
            user.setLangKey(scimUser.getLocale());
            user.setIdToken(tokenResponse.getIdToken());

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities().stream().map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList())));

            return user;
        } catch (IOException | JAXBException e) {
            throw new OPException(OPException.ERROR_LOGIN);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && request != null && response != null)
            new SecurityContextLogoutHandler().logout(request, response, auth);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public OPUser changePassword(String password) throws OPException {
        Optional<OPUser> opUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> authentication.getPrincipal())
            .filter(OPUser.class::isInstance)
            .map(OPUser.class::cast);
        OPUser user = opUser.orElseThrow(() -> new OPException(OPException.ERROR_PASSWORD_CHANGE));
        try {
            ScimResponse scimResponse = scimService.retrievePerson(user.getScimId());
            if (scimResponse.getStatusCode() == 200) {
                User scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
                scimUser.setPassword(password);
                scimResponse = scimService.updatePerson(scimUser, scimUser.getId());
                if (scimResponse.getStatusCode() != 200) {
                    throw new OPException(OPException.ERROR_PASSWORD_CHANGE);
                }
                return user;
            }
            throw new OPException(OPException.ERROR_PASSWORD_CHANGE);
        } catch (IOException | JAXBException e) {
            throw new OPException(OPException.ERROR_PASSWORD_CHANGE, e);
        }
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
                        logout(null, null);
                        return null;
                    }
                });
    }
}
