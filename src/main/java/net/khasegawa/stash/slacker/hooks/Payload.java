package net.khasegawa.stash.slacker.hooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazuki Hasegawa on 15/02/05.
 *
 * @author Kazuki Hasegawa
 */
public class Payload {
    public String channel;
    public String text;
    public String icon_emoji;
    public String emoji_url;
    public String username;
    public List<Attachment> attachments;

    public Payload() {
        this.attachments = new ArrayList<Attachment>();
    }
}
