package net.khasegawa.stash.slacker.hooks;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kazuki on 27/11/15.
 */
public class Field {
    public String title;
    public String value;
    @SerializedName("short")
    public boolean isShort;
}
