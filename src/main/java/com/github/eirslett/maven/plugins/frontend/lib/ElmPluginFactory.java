package com.github.eirslett.maven.plugins.frontend.lib;

import java.io.File;

public class ElmPluginFactory {
    private static final Platform defaultPlatform = Platform.guess();
    private static final String DEFAULT_CACHE_PATH = "cache";

    private final File workingDirectory;
    private final File installDirectory;
    private final CacheResolver cacheResolver;

    public ElmPluginFactory(File workingDirectory, File installDirectory){
        this(workingDirectory, installDirectory, getDefaultCacheResolver(installDirectory));
    }

    public ElmPluginFactory(File workingDirectory, File installDirectory, CacheResolver cacheResolver){
        this.workingDirectory = workingDirectory;
        this.installDirectory = installDirectory;
        this.cacheResolver = cacheResolver;
    }

    public ElmInstaller getElmInstaller(ProxyConfig proxy) {
        return new ElmInstaller(getInstallConfig(), new DefaultArchiveExtractor(), new DefaultFileDownloader(proxy));
    }
    public ElmRunner getElmRunner() {
        return new DefaultElmRunner(new InstallElmExecutorConfig(getInstallConfig()));
    }

    private InstallConfig getInstallConfig() {
        return new DefaultInstallConfig(installDirectory, workingDirectory, cacheResolver, defaultPlatform);
    }

    private static final CacheResolver getDefaultCacheResolver(File root) {
        return new DirectoryCacheResolver(new File(root, DEFAULT_CACHE_PATH));
    }
}
