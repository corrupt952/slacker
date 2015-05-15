package net.khasegawa.stash.slacker.activeobjects;

import net.java.ao.Entity;
import net.java.ao.schema.Default;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

/**
 * Created by Kazuki Hasegawa on 14/05/15.
 *
 * @author kazuki hasegawa
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

    public String getUserJSON();
    public void setUserJSON(String userJSON);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRCreated();
    public void setNotifyPRCreated(Boolean notifyPRCreated);

    @NotNull
    @Default("true")
    public Boolean getNotifyPRUpdated();
    public void setNotifyPRUpdated(Boolean notifyPRUpdated);

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
}
