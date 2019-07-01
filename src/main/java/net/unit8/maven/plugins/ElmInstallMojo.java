package net.unit8.maven.plugins;

import com.github.eirslett.maven.plugins.frontend.lib.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.crypto.SettingsDecrypter;

@Mojo(name = "install", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ElmInstallMojo extends AbstractElmMojo {
    @Parameter(property = "elmDownloadRoot", required = false,
            defaultValue = ElmInstaller.DEFAULT_ELM_DOWNLOAD_ROOT)
    private String elmDownloadRoot;

    @Parameter(property = "elmVersion", required = true)
    private String elmVersion;

    /**
     * Server Id for download username and password
     */
    @Parameter(property = "serverId", defaultValue = "")
    private String serverId;

    @Parameter(property = "session", defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Component(role = SettingsDecrypter.class)
    private SettingsDecrypter decrypter;


    @Parameter(property = "skip.installelm", alias = "skip.installelm", defaultValue = "${skip.installelm}")
    private boolean skip;

    @Override
    protected boolean skipExecution() {
        return this.skip;
    }

    @Override
    public void execute(ElmPluginFactory factory) throws InstallationException {
        ProxyConfig proxyConfig = MojoUtils.getProxyConfig(this.session, this.decrypter);
        Server server = MojoUtils.decryptServer(this.serverId, this.session, this.decrypter);
        if (null != server) {
            factory.getElmInstaller(proxyConfig).setElmDownloadRoot(this.elmDownloadRoot)
                    .setElmVersion(this.elmVersion).setUserName(server.getUsername())
                    .setPassword(server.getPassword()).install();
        } else {
            factory.getElmInstaller(proxyConfig).setElmDownloadRoot(this.elmDownloadRoot)
                    .setElmVersion(this.elmVersion).install();
        }
    }

}
