package org.gluu.credmgr.scim2.client;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;

public class ScimTest {

    @Test
    public void test() throws Exception {
	final String domain = "https://gluu.localhost.info/identity/seam/resource/restv1";
	final String umaMetaDataUrl = "https://gluu.localhost.info/.well-known/uma-configuration";
	final String umaAatClientId = "@!C0B0.0128.9A9D.9453!0001!EF7B.61C8!0008!1F9D.C1F5";

	final String umaAatClientJwks = IOUtils
		.toString(getClass().getClassLoader().getResourceAsStream("scim-rp-openid-keys.json"));
	final String umaAatClientKeyId = "";
	final Scim2Client scim2Client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId,
		umaAatClientJwks, umaAatClientKeyId);
	ScimResponse response = scim2Client.retrievePerson("@!C0B0.0128.9A9D.9453!0001!EF7B.61C8!0000!90AD.3805",
		MediaType.APPLICATION_JSON);

	System.out.println("SCIM1 " + response.getResponseBodyString());
	System.out.println("");

    }

}
