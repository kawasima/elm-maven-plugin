package net.unit8.maven.plugins;

import com.github.eirslett.maven.plugins.frontend.lib.ElmPluginFactory;
import com.github.eirslett.maven.plugins.frontend.lib.FrontendException;
import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;

import java.io.File;
import java.util.Map;

public abstract class AbstractElmMojo extends AbstractMojo {
    @Component
    protected MojoExecution execution;

    /**
     * Whether you should skip while running in the test phase (default is false)
     */
    @Parameter(property = "skipTests", required = false, defaultValue = "false")
    protected Boolean skipTests;

    /**
     * Set this to true to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     *
     * @since 1.4
     */
    @Parameter(property = "maven.test.failure.ignore", defaultValue = "false")
    protected boolean testFailureIgnore;

    /**
     * The base directory for running all Node commands. (Usually the directory that contains package.json)
     */
    @Parameter(defaultValue = "${basedir}", property = "workingDirectory", required = false)
    protected File workingDirectory;

    /**
     * The base directory for installing node and npm.
     */
    @Parameter(property = "installDirectory", required = false)
    protected File installDirectory;

    /**
     * Additional environment variables to pass to the build.
     */
    @Parameter
    protected Map<String, String> environmentVariables;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * Determines if this execution should be skipped.
     */
    private boolean skipTestPhase() {
        return skipTests && isTestingPhase();
    }

    /**
     * Determines if the current execution is during a testing phase (e.g., "test" or "integration-test").
     */
    private boolean isTestingPhase() {
        String phase = execution.getLifecyclePhase();
        return "test".equals(phase) || "integration-test".equals(phase);
    }

    protected abstract void execute(ElmPluginFactory factory) throws FrontendException;

    /**
     * Implemented by children to determine if this execution should be skipped.
     */
    protected abstract boolean skipExecution();

    @Override
    public void execute() throws MojoFailureException {
        if (testFailureIgnore && !isTestingPhase()) {
            getLog().info("testFailureIgnore property is ignored in non test phases");
        }
        if (!(skipTestPhase() || skipExecution())) {
            if (installDirectory == null) {
                installDirectory = workingDirectory;
            }
            try {
                execute(new ElmPluginFactory(workingDirectory, installDirectory,
                        new RepositoryCacheResolver(repositorySystemSession)));
            } catch (TaskRunnerException e) {
                if (testFailureIgnore && isTestingPhase()) {
                    getLog().error("There are test failures.\nFailed to run task: " + e.getMessage(), e);
                } else {
                    throw new MojoFailureException("Failed to run task", e);
                }
            } catch (FrontendException e) {
                throw toMojoFailureException(e);
            }
        } else {
            getLog().info("Skipping execution.");
        }

    }
    static <E extends Throwable> MojoFailureException toMojoFailureException(E e) {
        String causeMessage = e.getCause() != null ? ": " + e.getCause().getMessage() : "";
        return new MojoFailureException(e.getMessage() + causeMessage, e);
    }
}
