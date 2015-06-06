package net.khasegawa.stash.slacker.configurations;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.stash.project.Project;
import com.atlassian.stash.project.ProjectService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.util.PageRequestImpl;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.khasegawa.stash.slacker.activeobjects.ProjectConfiguration;
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
 * @author Kazuki Hasegawa
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    private final ActiveObjects activeObjects;
    private final ProjectService projectService;
    private final RepositoryService repositoryService;

    public ConfigurationServiceImpl(ActiveObjects activeObjects,
                                    ProjectService projectService,
                                    RepositoryService repositoryService) throws NullArgumentException {
        this.activeObjects = activeObjects;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
    }

    @Override
    public ProjectConfiguration getProjectConfiguration(Integer projectId) throws SQLException,
            NullArgumentException {
        if (projectId == null) throw new NullArgumentException("Project ID is not null!");

        ProjectConfiguration[] configurations = getProjectConfigurations(projectId);
        ProjectConfiguration configuration = null;
        if (configurations.length == 0) {
            configuration = activeObjects.create(
                    ProjectConfiguration.class,
                    new DBParam("PROJECT_ID", projectId),
                    new DBParam("USER_MAP_JSON", "{ \"Stash UserID\": \"Slack UserID\" }")
            );
        } else {
            configuration = configurations[0];
        }

        return configuration;
    }

    @Override
    public void setProjectConfigurationByHttpServletRequest(Integer projectId,
                                                            HttpServletRequest req) throws SQLException,
            NullArgumentException,
            NumberFormatException {
        String hookURL = req.getParameter("hookURL");
        String userMapJSON = req.getParameter("userMapJSON");

        setProjectConfiguration(projectId, hookURL, userMapJSON);
    }

    @Override
    public void setProjectConfiguration(Integer projectId,
                                        String hookURL,
                                        String userMapJSON) throws SQLException,
            NullArgumentException {
        if (projectId == null ) throw new NullArgumentException("Project ID is not null!");

        ProjectConfiguration[] configurations = getProjectConfigurations(projectId);

        if (configurations.length == 0) {
            activeObjects.create(
                    ProjectConfiguration.class,
                    new DBParam("PROJECT_ID", projectId),
                    new DBParam("HOOK_URL", hookURL),
                    new DBParam("USER_MAP_JSON", userMapJSON)
            );
            return;
        }

        ProjectConfiguration configuration = configurations[0];
        configuration.setHookURL(hookURL);
        configuration.setUserMapJSON(userMapJSON);
        configuration.save();
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
                    new DBParam("REPOSITORY_ID", repositoryId)
            );
        }

        return configurations[0];
    }

    @Override
    public void setRepositoryConfigurationByHttpServletRequest(Integer repositoryId,
                                                               HttpServletRequest req) throws SQLException,
            NullArgumentException,
            NumberFormatException {
        String channel = req.getParameter("channel");
        Boolean notifyPROpened = BooleanUtils.toBoolean(req.getParameter("notifyPROpened"));
        Boolean notifyPRReopened = BooleanUtils.toBoolean(req.getParameter("notifyPRReopened"));
        Boolean notifyPRUpdated = BooleanUtils.toBoolean(req.getParameter("notifyPRUpdated"));
        Boolean notifyPRRescoped = BooleanUtils.toBoolean(req.getParameter("notifyPRRescoped"));
        Boolean notifyPRMerged = BooleanUtils.toBoolean(req.getParameter("notifyPRMerged"));
        Boolean notifyPRDeclined = BooleanUtils.toBoolean(req.getParameter("notifyPRDeclined"));
        Boolean notifyPRCommented = BooleanUtils.toBoolean(req.getParameter("notifyPRCommented"));
        Boolean ignoreWIP = BooleanUtils.toBoolean(req.getParameter("ignoreWIP"));
        Boolean ignoreNotCrossRepository = BooleanUtils.toBoolean(req.getParameter("ignoreNotCrossRepository"));

        setRepositoryConfiguration(
                repositoryId,
                channel,
                notifyPROpened,
                notifyPRReopened,
                notifyPRUpdated,
                notifyPRRescoped,
                notifyPRMerged,
                notifyPRDeclined,
                notifyPRCommented,
                ignoreWIP,
                ignoreNotCrossRepository
        );
    }

    @Override
    public void setRepositoryConfiguration(Integer repositoryId,
                                           String channel,
                                           Boolean notifyPROpened,
                                           Boolean notifyPRReopened,
                                           Boolean notifyPRUpdated,
                                           Boolean notifyPRRescoped,
                                           Boolean notifyPRMerged,
                                           Boolean notifyPRDeclined,
                                           Boolean notifyPRCommented,
                                           Boolean ignoreWIP,
                                           Boolean ignoreNotCrossRepository) throws SQLException,
            NullArgumentException {
        if (repositoryId == null ) throw new NullArgumentException("Repository ID is not null!");

        RepositoryConfiguration[] configurations = activeObjects.find(
                RepositoryConfiguration.class,
                Query.select().where("REPOSITORY_ID = ?", repositoryId));

        if (configurations.length == 0) {
            activeObjects.create(
                    RepositoryConfiguration.class,
                    new DBParam("REPOSITORY_ID", repositoryId),
                    new DBParam("CHANNEL", channel),
                    new DBParam("NOTIFY_PR_OPENED", notifyPROpened),
                    new DBParam("NOTIFY_PR_REOPENED", notifyPRReopened),
                    new DBParam("NOTIFY_PR_UPDATED", notifyPRUpdated),
                    new DBParam("NOTIFY_PR_RESCOPED", notifyPRRescoped),
                    new DBParam("NOTIFY_PR_MERGED", notifyPRMerged),
                    new DBParam("NOTIFY_PR_DECLINED", notifyPRDeclined),
                    new DBParam("NOTIFY_PR_COMMENTED", notifyPRCommented),
                    new DBParam("IGNORE_WIP", ignoreWIP),
                    new DBParam("IGNORE_NOT_CROSS_REPOSITORY", ignoreNotCrossRepository)
            );
            return;
        }

        RepositoryConfiguration configuration = configurations[0];
        configuration.setChannel(channel);
        configuration.setNotifyPROpened(notifyPROpened);
        configuration.setNotifyPRReopend(notifyPRReopened);
        configuration.setNotifyPRUpdated(notifyPRUpdated);
        configuration.setNotifyPRRescoped(notifyPRRescoped);
        configuration.setNotifyPRMerged(notifyPRMerged);
        configuration.setNotifyPRDeclined(notifyPRDeclined);
        configuration.setNotifyPRCommented(notifyPRCommented);
        configuration.setIgnoreWIP(ignoreWIP);
        configuration.setIgnoreNotCrossRepository(ignoreNotCrossRepository);
        configuration.save();
    }

    private ProjectConfiguration[] getProjectConfigurations(Integer projectId) {
        return activeObjects.find(
                ProjectConfiguration.class,
                Query.select().where("PROJECT_ID = ?", projectId)
        );
    }
}
