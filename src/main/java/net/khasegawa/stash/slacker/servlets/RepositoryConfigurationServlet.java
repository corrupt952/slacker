package net.khasegawa.stash.slacker.servlets;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.user.AuthenticationException;
import com.atlassian.stash.user.Permission;
import com.atlassian.stash.user.PermissionValidationService;
import com.google.common.collect.ImmutableMap;
import net.khasegawa.stash.slacker.activeobjects.RepositoryConfiguration;
import net.khasegawa.stash.slacker.configurations.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class RepositoryConfigurationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryConfigurationServlet.class);

    private final PermissionValidationService permissionValidationService;
    private final RepositoryService repositoryService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final ConfigurationService configurationService;

    public RepositoryConfigurationServlet(RepositoryService repositoryService,
                                          PermissionValidationService permissionValidationService,
                                          SoyTemplateRenderer soyTemplateRenderer,
                                          ConfigurationService configurationService) {
        this.permissionValidationService = permissionValidationService;
        this.repositoryService = repositoryService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.configurationService = configurationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Repository repository = searchRepositoryByRequest(req);
        if (repository == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            permissionValidationService.validateForRepository(repository, Permission.REPO_ADMIN);
        } catch (AuthenticationException e) {
            logger.warn("User {} tried to access the slacker page for {}",
                        req.getRemoteUser(), repository.getSlug());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        resp.setContentType("text/html;charset=UTF-8");

        try {
            RepositoryConfiguration configuration = configurationService.getRepositoryConfiguration(repository.getId());

            this.soyTemplateRenderer.render(
                    resp.getWriter(),
                    "net.khasegawa.stash.slacker:slacker-configuration",
                    "plugin.page.slacker.slackerConfigurationPanel",
                    ImmutableMap
                            .<String, Object>builder()
                            .put("repository", repository)
                            .put("hookURL", StringUtils.defaultString(configuration.getHookURL()))
                            .put("channel", StringUtils.defaultString(configuration.getChannel()))
                            .put("userJSON", StringUtils.defaultString(configuration.getUserJSON()))
                            .put("notifyPROpened", configuration.getNotifyPROpened())
                            .put("notifyPRReopened", configuration.getNotifyPRReopened())
                            .put("notifyPRUpdated", configuration.getNotifyPRUpdated())
                            .put("notifyPRRescoped", configuration.getNotifyPRRescoped())
                            .put("notifyPRMerged", configuration.getNotifyPRMerged())
                            .put("notifyPRDeclined", configuration.getNotifyPRDeclined())
                            .put("notifyPRCommented", configuration.getNotifyPRCommented())
                            .put("ignoreWIP", configuration.getIgnoreWIP())
                            .put("ignoreNotCrossRepository", configuration.getIgnoreNotCrossRepository())
                            .build()
            );
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw new ServletException(e);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Repository repository = searchRepositoryByRequest(req);
        if (repository == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            permissionValidationService.validateForRepository(repository, Permission.REPO_ADMIN);
        } catch (AuthenticationException e) {
            logger.warn("User {} tried to access the slacker page for {}",
                    req.getRemoteUser(), repository.getSlug());
            doGet(req, resp);
            return;
        }

        try {
            configurationService.setRepositoryConfigurationByHttpServletRequest(repository.getId(), req);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        doGet(req, resp);
    }

    private Repository searchRepositoryByRequest(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return null;

        path = path.startsWith("/") ? path.substring(1) : path;
        String[] paths = path.split("/");
        if (paths.length != 3) return null;

        return repositoryService.getBySlug(paths[1], paths[2]);
    }
}
