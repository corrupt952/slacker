package net.khasegawa.stash.slacker.activeobjects;

import net.java.ao.Entity;
import net.java.ao.schema.*;

/**
 * Created by Kazuki Hasegawa on 14/06/06.
 *
 * @author Kazuki Hasegawa
 */
@Table("SlakcerProjConf")

public interface ProjectConfiguration extends Entity {
    @NotNull
    @Unique
    public Integer getProjectId();
    public void setProjectId(Integer projectId);

    public String getHookURL();
    public void setHookURL(String hookURL);

    public String getUserMapJSON();
    @StringLength(value=StringLength.UNLIMITED)
    public void setUserMapJSON(String userMapJSON);
}
