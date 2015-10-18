package net.khasegawa.stash.slacker.servlets;

import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.google.common.collect.ImmutableMap;
import net.khasegawa.stash.slacker.activeobjects.ProjectConfiguration;
import net.khasegawa.stash.slacker.configurations.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ProjectConfigurationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProjectConfigurationServlet.class);

    private final PermissionValidationService permissionValidationService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final ProjectService projectService;
    private final ConfigurationService configurationService;

    public ProjectConfigurationServlet(PermissionValidationService permissionValidationService,
                                       SoyTemplateRenderer soyTemplateRenderer,
                                       ProjectService projectService,
                                       ConfigurationService configurationService) {
        this.permissionValidationService = permissionValidationService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.projectService = projectService;
        this.configurationService = configurationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Project project = searchProjectByRequest(req);
        if (project == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            permissionValidationService.validateForProject(project, Permission.PROJECT_ADMIN);
        } catch (AuthorisationException e) {
            logger.warn("User {} tried to access the slacker page for {}", project.getKey());
            doGet(req, resp);
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");

        try {
            ProjectConfiguration configuration = configurationService.getProjectConfiguration(project.getId());

            this.soyTemplateRenderer.render(
                    resp.getWriter(),
                    "net.khasegawa.stash.slacker:slacker-configuration",
                    "plugin.page.slacker.slackerProjectConfigurationPanel",
                    ImmutableMap
                            .<String, Object>builder()
                            .put("project", project)
                            .put("hookURL", StringUtils.defaultString(configuration.getHookURL()))
                            .put("channel", StringUtils.defaultString(configuration.getChannel()))
                            .put("notifyPROpened", configuration.getNotifyPROpened())
                            .put("notifyPRReopened", configuration.getNotifyPRReopened())
                            .put("notifyPRUpdated", configuration.getNotifyPRUpdated())
                            .put("notifyPRRescoped", configuration.getNotifyPRRescoped())
                            .put("notifyPRMerged", configuration.getNotifyPRMerged())
                            .put("notifyPRDeclined", configuration.getNotifyPRDeclined())
                            .put("notifyPRCommented", configuration.getNotifyPRCommented())
                            .put("ignoreWIP", configuration.getIgnoreWIP())
                            .put("ignoreNotCrossRepository", configuration.getIgnoreNotCrossRepository())
                            .put("userMapJSON", StringUtils.defaultString(configuration.getUserMapJSON()))
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
        Project project = searchProjectByRequest(req);
        if (project == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            permissionValidationService.validateForProject(project, Permission.PROJECT_ADMIN);
        } catch (AuthorisationException e) {
            logger.warn("User {} tried to access the slacker page for {}", project.getKey());
            doGet(req, resp);
            return;
        }

        try {
            logger.error(req.getParameter("hookURL"));
            configurationService.setProjectConfigurationByHttpServletRequest(project.getId(), req);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        doGet(req, resp);
    }

    private Project searchProjectByRequest(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return null;

        path = path.startsWith("/") ? path.substring(1) : path;
        String[] paths = path.split("/");
        if (paths.length != 1) return null;

        return projectService.getByKey(paths[0]);
    }
}
