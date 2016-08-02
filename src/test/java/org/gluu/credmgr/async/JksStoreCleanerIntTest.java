package org.gluu.credmgr.async;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.util.RandomUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by eugeniuparvan on 8/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class JksStoreCleanerIntTest {

    @Inject
    @Qualifier("jksStoreCleaner")
    private Cleaner jksStoreCleaner;

    @Inject
    private OPConfigRepository opConfigRepository;

    @Test
    @Transactional
    public void testClean() {
        OPConfig opConfig = new OPConfig();
        opConfig.setAdminScimId("");
        opConfig.setActivated(false);
        opConfig.setEmail("mail@mail.com");
        opConfig.setActivationKey(RandomUtil.generateActivationKey());
        opConfig.setCompanyName("company_name");
        opConfig.setCompanyShortName("company_short_name");
        opConfig.setClientJKS("/asd/Hello.jks");
        opConfig = opConfigRepository.save(opConfig);
        Assert.assertNotNull(opConfig.getClientJKS());
        jksStoreCleaner.clean();
        Assert.assertNull(opConfig.getClientJKS());
    }
}
