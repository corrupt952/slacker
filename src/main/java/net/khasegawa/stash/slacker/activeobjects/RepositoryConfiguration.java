package net.khasegawa.stash.slacker.activeobjects;

import net.java.ao.Entity;
import net.java.ao.schema.*;

/**
 * Created by Kazuki Hasegawa on 14/05/15.
 *
 * @author Kazuki Hasegawa
 */
@Table("SlakcerRepoConf")
public interface RepositoryConfiguration extends Entity {
    @NotNull
    @Unique
    public Integer getRepositoryId();
    public void setRepositoryId(Integer repositoryId);

    public String getHookURL();
    public void setHookURL(String hookURL);

    public String getChannel();
    public void setChannel(String channel);

    @NotNull
    @Default("true")
    public Boolean getNotifyPROpened();
    public void setNotifyPROpened(Boolean notifyPROpened);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRReopened();
    public void setNotifyPRReopened(Boolean notifyPRReopened);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRUpdated();
    public void setNotifyPRUpdated(Boolean notifyPRUpdated);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRRescoped();
    public void setNotifyPRRescoped(Boolean notifyPRRescoped);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRMerged();
    public void setNotifyPRMerged(Boolean notifyPRMerged);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRDeclined();
    public void setNotifyPRDeclined(Boolean notifyPRDeclined);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRCommented();
    public void setNotifyPRCommented(Boolean notifyPRCommented);

    @NotNull
    @Default("false")
    public Boolean getIgnoreWIP();
    public void setIgnoreWIP(Boolean ignoreWIP);

    @NotNull
    @Default("false")
    public Boolean getIgnoreNotCrossRepository();
    public void setIgnoreNotCrossRepository(Boolean ignoreNotCrossRepository);

    public String getUserMapJSON();
    @StringLength(value=StringLength.UNLIMITED)
    public void setUserMapJSON(String userMapJSON);
}
