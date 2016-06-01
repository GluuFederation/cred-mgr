package org.gluu.credmgr.web.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by eugeniuparvan on 5/30/16.
 */
@RestController
@RequestMapping("/api")
public class OpenIdAccountResource {

    @RequestMapping("/{companyName}/user")
    public void getUserInfo(@PathVariable("companyName") String companyName) {
    }

    @RequestMapping("/openid/login-redirect")
    public void loginRedirectionHandler(HttpServletResponse response,
                                        @RequestParam(required = false, value = "session_state") String sessionState,
                                        @RequestParam(required = false, value = "scope") String scope,
                                        @RequestParam(required = false, value = "state") String state,
                                        @RequestParam(required = false, value = "code") String code) throws IOException {
        response.sendRedirect("/#/reset-password");
    }

    @RequestMapping("/openid/logout-redirect")
    public void logoutRedirectionHandler() {

    }


}
