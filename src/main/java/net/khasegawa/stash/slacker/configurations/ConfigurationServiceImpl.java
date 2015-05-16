package net.khasegawa.stash.slacker.configurations;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.khasegawa.stash.slacker.activeobjects.RepositoryConfiguration;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by Kazuki Hasegawa on 14/05/15.
 *
 * @author kazuki hasegawa
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    private final ActiveObjects activeObjects;

    public ConfigurationServiceImpl(ActiveObjects activeObjects) throws NullArgumentException {
        this.activeObjects = activeObjects;
    }

    @Override
    public RepositoryConfiguration getRepositoryConfiguration(Integer repositoryId) throws SQLException,
            NullArgumentException {
        if (repositoryId == null ) throw new NullArgumentException("Repository ID is not null!");

        RepositoryConfiguration[] configurations = activeObjects.find(
                RepositoryConfiguration.class,
                Query.select().where("REPOSITORY_ID = ?", repositoryId));

        if (configurations.length == 0) {
            return activeObjects.create(
                    RepositoryConfiguration.class,
                    new DBParam("REPOSITORY_ID", repositoryId),
                    new DBParam("USER_JSON", "{ \"Stash UserID\": \"Slack UserID\" }")
            );
        }

        return configurations[0];
    }

    @Override
    public void setRepositoryConfigurationByHttpServletRequest(Integer repositoryId,
                                                               HttpServletRequest req) throws SQLException,
            NullArgumentException,
            NumberFormatException {
        String hookURL = req.getParameter("hookURL");
        String channel = req.getParameter("channel");
        String userJSON = req.getParameter("userJSON");
        Boolean notifyPROpened = BooleanUtils.toBoolean(req.getParameter("notifyPROpened"));
        Boolean notifyPRReopened = BooleanUtils.toBoolean(req.getParameter("notifyPRReopened"));
        Boolean notifyPRUpdated = BooleanUtils.toBoolean(req.getParameter("notifyPRUpdated"));
        Boolean notifyPRRescoped = BooleanUtils.toBoolean(req.getParameter("notifyPRRescoped"));
        Boolean notifyPRMerged = BooleanUtils.toBoolean(req.getParameter("notifyPRMerged"));
        Boolean notifyPRDeclined = BooleanUtils.toBoolean(req.getParameter("notifyPRDeclined"));
        Boolean notifyPRCommented = BooleanUtils.toBoolean(req.getParameter("notifyPRCommented"));


        setRepositoryConfiguration(
                repositoryId,
                hookURL,
                channel,
                userJSON,
                notifyPROpened,
                notifyPRReopened,
                notifyPRUpdated,
                notifyPRRescoped,
                notifyPRMerged,
                notifyPRDeclined,
                notifyPRCommented
        );
    }

    @Override
    public void setRepositoryConfiguration(Integer repositoryId,
                                           String hookURL,
                                           String channel,
                                           String userJSON,
                                           Boolean notifyPROpened,
                                           Boolean notifyPRReopened,
                                           Boolean notifyPRUpdated,
                                           Boolean notifyPRRescoped,
                                           Boolean notifyPRMerged,
                                           Boolean notifyPRDeclined,
                                           Boolean notifyPRCommented) throws SQLException,
            NullArgumentException {
        if (repositoryId == null ) throw new NullArgumentException("Repository ID is not null!");

        RepositoryConfiguration[] configurations = activeObjects.find(
                RepositoryConfiguration.class,
                Query.select().where("REPOSITORY_ID = ?", repositoryId));

        if (configurations.length == 0) {
            activeObjects.create(
                    RepositoryConfiguration.class,
                    new DBParam("REPOSITORY_ID", repositoryId),
                    new DBParam("HOOK_URL", hookURL),
                    new DBParam("CHANNEL", channel),
                    new DBParam("USER_JSON", userJSON),
                    new DBParam("NOTIFY_PR_OPENED", notifyPROpened),
                    new DBParam("NOTIFY_PR_REOPENED", notifyPRReopened),
                    new DBParam("NOTIFY_PR_UPDATED", notifyPRUpdated),
                    new DBParam("NOTIFY_PR_RESCOPED", notifyPRRescoped),
                    new DBParam("NOTIFY_PR_MERGED", notifyPRMerged),
                    new DBParam("NOTIFY_PR_DECLINED", notifyPRDeclined),
                    new DBParam("NOTIFY_PR_COMMENTED", notifyPRCommented)
            );
            return;
        }

        RepositoryConfiguration configuration = configurations[0];
        configuration.setHookURL(hookURL);
        configuration.setChannel(channel);
        configuration.setUserJSON(userJSON);
        configuration.setNotifyPROpened(notifyPROpened);
        configuration.setNotifyPRReopend(notifyPRReopened);
        configuration.setNotifyPRUpdated(notifyPRUpdated);
        configuration.setNotifyPRRescoped(notifyPRRescoped);
        configuration.setNotifyPRMerged(notifyPRMerged);
        configuration.setNotifyPRDeclined(notifyPRDeclined);
        configuration.setNotifyPRCommented(notifyPRCommented);
        configuration.save();
    }
}
