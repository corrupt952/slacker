package net.khasegawa.stash.slacker.configurations;

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
    public abstract RepositoryConfiguration getRepositoryConfiguration(Integer repositoryId) throws SQLException,
            NullArgumentException;

    public abstract void setRepositoryConfigurationByHttpServletRequest(Integer repositoryId,
                                                                        HttpServletRequest req) throws SQLException,
            NullArgumentException,
            NumberFormatException;

    public abstract void setRepositoryConfiguration(Integer repositoryId,
                                                    String hookURL,
                                                    String channel,
                                                    String userJSON,
                                                    Boolean notifyPROepened,
                                                    Boolean notifyPRReopened,
                                                    Boolean notifyPRUpdated,
                                                    Boolean notifyPRReescoped,
                                                    Boolean notifyPRMerged,
                                                    Boolean notifyPRDeclined,
                                                    Boolean notifyPRCommented) throws SQLException,
            NullArgumentException;
}
