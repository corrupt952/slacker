package net.khasegawa.stash.slacker.configurations;

import net.khasegawa.stash.slacker.activeobjects.ProjectConfiguration;
import net.khasegawa.stash.slacker.activeobjects.RepositoryConfiguration;
import org.apache.commons.lang.NullArgumentException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by Kazuki Hasegawa on 15/05/15.
 *
 * @author Kazuki hasegawa
 */
public interface ConfigurationService {
    public abstract ProjectConfiguration getProjectConfiguration(Integer projectId) throws SQLException,
            NullArgumentException;

    public abstract void setProjectConfigurationByHttpServletRequest(Integer projectId,
                                                                     HttpServletRequest req) throws SQLException;

    public abstract boolean existsProjectConfiguration(Integer projectId) throws SQLException;

    public abstract  void setProjectConfiguration(Integer projectId,
                                                  String hookURL,
                                                  String channel,
                                                  Boolean notifyPROepened,
                                                  Boolean notifyPRReopened,
                                                  Boolean notifyPRUpdated,
                                                  Boolean notifyPRReescoped,
                                                  Boolean notifyPRMerged,
                                                  Boolean notifyPRDeclined,
                                                  Boolean notifyPRCommented,
                                                  Boolean ignoreWIP,
                                                  Boolean ignoreNotCrossRepository,
                                                  String userMapJSON) throws  SQLException;

    public abstract RepositoryConfiguration getRepositoryConfiguration(Integer repositoryId) throws SQLException,
            NullArgumentException;

    public abstract boolean existsRepositoryConfiguration(Integer repositoryId) throws SQLException;

    public abstract void setRepositoryConfigurationByHttpServletRequest(Integer repositoryId,
                                                                        HttpServletRequest req) throws SQLException,
            NullArgumentException,
            NumberFormatException;

    public abstract void setRepositoryConfiguration(Integer repositoryId,
                                                    String hookURL,
                                                    String channel,
                                                    Boolean notifyPROepened,
                                                    Boolean notifyPRReopened,
                                                    Boolean notifyPRUpdated,
                                                    Boolean notifyPRReescoped,
                                                    Boolean notifyPRMerged,
                                                    Boolean notifyPRDeclined,
                                                    Boolean notifyPRCommented,
                                                    Boolean ignoreWIP,
                                                    Boolean ignoreNotCrossRepository,
                                                    String userMapJSON) throws  SQLException,
            NullArgumentException;
}
