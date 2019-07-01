package com.github.eirslett.maven.plugins.frontend.lib;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ElmInstallerTest {
    private static final Platform defaultPlatform = Platform.guess();
    private static final String DEFAULT_CACHE_PATH = "cache";

    @Test
    void test() throws InstallationException, IOException {
        final InstallConfig installConfig = new DefaultInstallConfig(
                new File(".").getCanonicalFile(),
                new File(".").getCanonicalFile(),
                new DirectoryCacheResolver(new File(".", DEFAULT_CACHE_PATH)),
                defaultPlatform
        );

        final DefaultArchiveExtractor archiveExtractor = new DefaultArchiveExtractor();
        final ProxyConfig proxyConfig = new ProxyConfig(new ArrayList<ProxyConfig.Proxy>());
        final DefaultFileDownloader fileDownloader = new DefaultFileDownloader(proxyConfig);
        final ElmInstaller elmInstaller = new ElmInstaller(installConfig, archiveExtractor, fileDownloader);
        elmInstaller.setElmVersion("0.19.0");
        elmInstaller.install();
    }

    @Test
    void execute() throws IOException, TaskRunnerException {
        final InstallConfig installConfig = new DefaultInstallConfig(
                new File(".").getCanonicalFile(),
                new File(".").getCanonicalFile(),
                new DirectoryCacheResolver(new File(".", DEFAULT_CACHE_PATH)),
                defaultPlatform
        );

        final ElmRunner elmRunner = new DefaultElmRunner(new InstallElmExecutorConfig(installConfig));
        elmRunner.execute("make", new HashMap<String, String>());
    }

    @Test
    void yarn() throws InstallationException {
        final InstallConfig installConfig = new DefaultInstallConfig(
                new File("."),
                new File("."),
                new DirectoryCacheResolver(new File(".", DEFAULT_CACHE_PATH)),
                defaultPlatform
        );

        final DefaultArchiveExtractor archiveExtractor = new DefaultArchiveExtractor();
        final ProxyConfig proxyConfig = new ProxyConfig(new ArrayList<ProxyConfig.Proxy>());
        final DefaultFileDownloader fileDownloader = new DefaultFileDownloader(proxyConfig);
        final YarnInstaller elmInstaller = new YarnInstaller(installConfig, archiveExtractor, fileDownloader);
        elmInstaller.setYarnVersion("v1.17.2");
        elmInstaller.install();

    }
}