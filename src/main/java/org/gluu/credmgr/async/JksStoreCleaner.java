package org.gluu.credmgr.async;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eugeniuparvan on 8/2/16.
 */
@Component("jksStoreCleaner")
@Transactional
public class JksStoreCleaner implements Cleaner, ResourceLoaderAware {

    private final Logger log = LoggerFactory.getLogger(NotActivatedUsersCleaner.class);

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private OPConfigRepository opConfigRepository;

    private ResourceLoader resourceLoader;

    /**
     * Jks files without reference in op_config table and references without files should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void clean() {

        List<OPConfig> opConfigList = opConfigRepository.findAll();

        Map<String, OPConfig> opConfigMap = new HashMap<>();
        for (OPConfig opConfig : opConfigList) {
            if (StringUtils.isEmpty(opConfig.getClientJKS()))
                continue;
            String folderName = getFolderName(opConfig);
            if (folderName == null) {
                opConfig.setClientJKS(null);
                opConfigRepository.save(opConfig);
            } else {
                opConfigMap.put(folderName, opConfig);
            }
        }

        // fetching all dir names in credmgr storage
        File file = new File(credmgrProperties.getJksStorePath());
        file.list((File current, String name) -> {
            File currentFile = new File(current, name);
            boolean isDir = currentFile.isDirectory();
            if (isDir) {
                if (opConfigMap.containsKey(name)) {
                    File jks = new File(credmgrProperties.getJksStorePath() + opConfigMap.get(name).getClientJKS());
                    if (!jks.exists()) {
                        removeDirAndRef(currentFile, opConfigMap.get(name));
                    }
                } else {
                    removeDirAndRef(currentFile, null);
                }
            }
            return false;
        });
    }

    private void removeDirAndRef(File file, OPConfig opConfig) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            log.error("Can't delete directory:" + file.getAbsolutePath(), e);
        }

        if (opConfig == null)
            return;

        opConfig.setClientJKS(null);
        opConfigRepository.save(opConfig);
    }

    private String getFolderName(OPConfig opConfig) {
        String path = opConfig.getClientJKS();
        String[] array = path.split("/");
        if (array.length != 3)
            return null;
        return array[1];
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
