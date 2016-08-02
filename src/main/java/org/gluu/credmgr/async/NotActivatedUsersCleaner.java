package org.gluu.credmgr.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by eugeniuparvan on 8/2/16.
 */
@Component
public class NotActivatedUsersCleaner implements Cleaner {

    private final Logger log = LoggerFactory.getLogger(NotActivatedUsersCleaner.class);

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void clean() {
        //TODO: to implement
//        ZonedDateTime now = ZonedDateTime.now();
//        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
//        for (User user : users) {
//            log.debug("Deleting not activated user {}", user.getLogin());
//            userRepository.delete(user);
//        }
    }
}
