package org.gluu.credmgr.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by eugeniuparvan on 5/30/16.
 */
@RestController
@RequestMapping("/api")
public class OpenIdResource {

    @RequestMapping("/{companyName}/user")
    public void getUserInfo(@PathVariable("companyName") String companyName) {
    }

}
