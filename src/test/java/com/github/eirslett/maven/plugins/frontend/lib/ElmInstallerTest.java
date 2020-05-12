package com.github.eirslett.maven.plugins.frontend.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElmInstallerTest {
    private static final Platform defaultPlatform = Platform.guess();
    private static final String DEFAULT_CACHE_PATH = "cache";
    private static final String ELM_VERSION_19_0 = "0.19.0";
    private static final String ELM_VERSION_19_1 = "0.19.1";
    private static final String ELM_DOWNLOAD_ROOT = ElmInstaller.DEFAULT_ELM_DOWNLOAD_ROOT;
    private static final String TARGET_NODE_ELM_ELM = "target/node/elm/elm";

    @BeforeEach
    void setUp() throws IOException {
        File file = new File(TARGET_NODE_ELM_ELM);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
    }

    @Test
    void test_install_0_19_0() throws InstallationException, IOException {
        final ElmInstaller elmInstaller = getElmInstaller();
        elmInstaller.setElmVersion(ELM_VERSION_19_0);
        elmInstaller.install();

        File file = new File(TARGET_NODE_ELM_ELM);
        assertTrue(file.exists());
        assertTrue(file.canExecute());
    }

    @Test
    void test_install_0_19_1() throws InstallationException, IOException {
        final ElmInstaller elmInstaller = getElmInstaller();
        elmInstaller.setElmVersion(ELM_VERSION_19_1);
        elmInstaller.install();

        File file = new File(TARGET_NODE_ELM_ELM);
        assertTrue(file.exists());
        assertTrue(file.canExecute());
    }

    @Test
    void downLoadUrlForVersion19_0() throws IOException {
        final ElmInstaller elmInstaller = getElmInstaller();
        elmInstaller.setElmVersion(ELM_VERSION_19_0);
        String platform = elmInstaller.getPlatform();

        String downloadUrl = elmInstaller.getDownloadUrl(ELM_DOWNLOAD_ROOT, ELM_VERSION_19_0);

        assertEquals(ELM_DOWNLOAD_ROOT + ELM_VERSION_19_0 + "/binaries-for-" + platform + ".tar.gz", downloadUrl);
    }

    @Test
    void downLoadUrlForVersion19_1() throws IOException {
        final ElmInstaller elmInstaller = getElmInstaller();
        elmInstaller.setElmVersion(ELM_VERSION_19_1);
        String platform = elmInstaller.getPlatform();

        String downloadUrl = elmInstaller.getDownloadUrl(ELM_DOWNLOAD_ROOT, ELM_VERSION_19_1);

        assertEquals(ELM_DOWNLOAD_ROOT + ELM_VERSION_19_1 + "/binary-for-" + platform + "-64-bit.gz", downloadUrl);
    }

    private ElmInstaller getElmInstaller() throws IOException {
        final InstallConfig installConfig = new DefaultInstallConfig(
                new File("target").getCanonicalFile(),
                new File("target").getCanonicalFile(),
                new DirectoryCacheResolver(new File("target/", DEFAULT_CACHE_PATH)),
                defaultPlatform
        );

        final DefaultArchiveExtractor archiveExtractor = new DefaultArchiveExtractor();
        final ProxyConfig proxyConfig = new ProxyConfig(new ArrayList<>());
        final DefaultFileDownloader fileDownloader = new DefaultFileDownloader(proxyConfig);
        return new ElmInstaller(installConfig, archiveExtractor, fileDownloader);
    }
}