package net.khasegawa.stash.slacker.hooks;

import com.atlassian.event.api.EventListener;
import com.atlassian.stash.event.pull.*;
import com.atlassian.stash.pull.PullRequestAction;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.google.gson.Gson;
import net.khasegawa.stash.slacker.activeobjects.ProjectConfiguration;
import net.khasegawa.stash.slacker.activeobjects.RepositoryConfiguration;
import net.khasegawa.stash.slacker.configurations.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Kazuki Hasegawa on 15/02/04.
 *
 * @author Kazuki Hasegawa
 */
public class PullRequestListener {
    private static final Logger logger = Logger.getLogger(PullRequestListener.class);

    private final ApplicationPropertiesService propertiesService;
    private final ConfigurationService configurationService;

    public PullRequestListener(ApplicationPropertiesService propertiesService,
                               ConfigurationService configurationService) {
        this.propertiesService = propertiesService;
        this.configurationService = configurationService;
    }

    @EventListener
    public void listenPullRequestOpenedEvent(PullRequestOpenedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestReopenedEvent(PullRequestReopenedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestMergedEvent(PullRequestMergedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestDeclinedEvent(PullRequestDeclinedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestUpdatedEvent(PullRequestUpdatedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestCommentAddedEvent(PullRequestCommentAddedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestCommentRepliedEvent(PullRequestCommentRepliedEvent event) {
        notifySlack(event);
    }

    @EventListener
    public void listenPullRequestRescopedEvent(PullRequestRescopedEvent event) {
        notifySlack(event);
    }

    public void notifySlack(PullRequestEvent event) {
        String username = event.getUser().getDisplayName();
        String repoName = event.getPullRequest().getToRef().getRepository().getName();
        Long id = event.getPullRequest().getId();
        Repository repo = event.getPullRequest().getToRef().getRepository();
        PullRequestAction action = event.getAction();
        Payload payload = new Payload();
        String url = String.format("%s/projects/%s/repos/%s/pull-requests/%d/overview",
                                   propertiesService.getBaseUrl(), repo.getProject().getKey(), repo.getSlug(),
                                   event.getPullRequest().getId());

        if (id == null) return;

        ProjectConfiguration projectConfiguration = null;
        RepositoryConfiguration configuration = null;
        try {
            projectConfiguration = configurationService.getProjectConfiguration(repo.getProject().getId());
            configuration = configurationService.getRepositoryConfiguration(repo.getId());
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return;
        }
        if (projectConfiguration == null) return;
        if (configuration == null) return;
        if (StringUtils.isBlank(projectConfiguration.getHookURL())) {
            logger.warn("Slack hook url is blank.");
            return;
        }
        if (configuration.getIgnoreWIP() &&
                Pattern.compile("^\\[?WIP\\]?").matcher(event.getPullRequest().getTitle()).find()) return;
        if (configuration.getIgnoreNotCrossRepository() && !event.getPullRequest().isCrossRepository()) return;

        if (StringUtils.isNotBlank(configuration.getChannel())) {
            payload.channel = String.format("%s", configuration.getChannel());
        }
        if (action == PullRequestAction.OPENED) {
            if (!configuration.getNotifyPROpened()) return;

            String title = event.getPullRequest().getTitle();
            Attachment attachment = new Attachment();

            attachment.pretext = String.format("%s opened PullRequest <%s|#%d> on %s", username, url, id, repoName);
            attachment.fallback = String.format("%s opened PullRequest #%d on %s - %s - %s", username, id, repoName, url, title);
            attachment.title = event.getPullRequest().getTitle();
            attachment.title_link = url;
            attachment.color = "#36a64f";
            attachment.text = event.getPullRequest().getDescription();
            payload.attachments.add(attachment);
        } else if (action == PullRequestAction.REOPENED) {
            if (!configuration.getNotifyPRReopened()) return;

            payload.text = String.format("%s reopened PullRequest <%s|#%d> on %s", username, url, id, repoName);
        } else if (action == PullRequestAction.MERGED) {
            if (!configuration.getNotifyPRMerged()) return;

            payload.text = String.format("%s merged PullRequest <%s|#%d> on %s", username, url, id, repoName);
        } else if (action == PullRequestAction.DECLINED) {
            if (!configuration.getNotifyPRDeclined()) return;

            payload.text = String.format("%s declined PullRequest <%s|#%d> on %s", username, url, id, repoName);
        } else if (action == PullRequestAction.UPDATED) {
            if (!configuration.getNotifyPRUpdated()) return;

            payload.text = String.format("%s updated PullRequest <%s|#%d> on %s", username, url, id, repoName);
        } else if (action == PullRequestAction.RESCOPED) {
            if (!configuration.getNotifyPRRescoped()) return;

            payload.text = String.format("%s rescoped PullRequest <%s|#%s> on %s", username, url, id, repoName);
        } else if(action == PullRequestAction.COMMENTED) {
            if (!configuration.getNotifyPRCommented()) return;
            if (StringUtils.isBlank(projectConfiguration.getUserMapJSON())) return;

            PullRequestCommentEvent commentEvent = (PullRequestCommentEvent) event;
            Map<String, String> userMap = new Gson().fromJson(projectConfiguration.getUserMapJSON(), HashMap.class);
            StashUser author = null;
            StashUser user = null;
            if (event instanceof PullRequestCommentAddedEvent) {
                author = commentEvent.getPullRequest().getAuthor().getUser();
                user = commentEvent.getUser();
            } else if (event instanceof PullRequestCommentRepliedEvent) {
                author = commentEvent.getParent().getAuthor();
                user = commentEvent.getUser();
            }

            if (author == null || !userMap.containsKey(author.getName()) ||
                    user == null || !userMap.containsKey(user.getName()) || user.getId() == author.getId()) return;

            String commentUrl = String.format("%s?commentId=%d", url, commentEvent.getComment().getId());

            payload.channel = String.format("@%s", userMap.get(author.getName()));
            payload.username = userMap.get(user.getName());
            payload.text = String.format("%s commented to PullRequest <%s|#%d> on %s: <%s|Show>",
                                         username, url, id, repoName, commentUrl);
        } else return;

        try {
            Gson gson = new Gson();
            Form form = Form.form().add("payload", gson.toJson(payload));
            HttpResponse response = Request
                    .Post(projectConfiguration.getHookURL())
                    .bodyForm(form.build(), Charset.forName("UTF-8"))
                    .execute()
                    .returnResponse();
            logger.info(response.getStatusLine());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
