package net.khasegawa.stash.slacker.hooks;

import java.util.List;

/**
 * Created by Kazuki Hasegawa on 15/02/05.
 *
 * @author Kazuki hasegawa
 */
public class Attachment {
    public String pretext;
    public String text;
    public String fallback;
    public String title;
    public String title_link;
    public String color;
    public List<Field> fields;
}
