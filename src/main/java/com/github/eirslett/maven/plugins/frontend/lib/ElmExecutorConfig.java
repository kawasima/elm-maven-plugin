package com.github.eirslett.maven.plugins.frontend.lib;

import java.io.File;

public interface ElmExecutorConfig {
    File getNodePath();

    File getElmPath();

    File getWorkingDirectory();

    Platform getPlatform();
}

final class InstallElmExecutorConfig implements ElmExecutorConfig {

    private static final String ELM_WINDOWS =
            ElmInstaller.INSTALL_PATH.concat("/elm.exe").replaceAll("/", "\\\\");

    private static final String ELM_DEFAULT = ElmInstaller.INSTALL_PATH + "/elm";

    private File nodePath;

    private final InstallConfig installConfig;

    public InstallElmExecutorConfig(InstallConfig installConfig) {
        this.installConfig = installConfig;
        nodePath = new InstallNodeExecutorConfig(installConfig).getNodePath();
    }

    @Override
    public File getNodePath() {
        return nodePath;
    }

    @Override
    public File getElmPath() {
        String elmExecutable = getPlatform().isWindows() ? ELM_WINDOWS : ELM_DEFAULT;
        return new File(installConfig.getInstallDirectory() + elmExecutable);
    }

    @Override
    public File getWorkingDirectory() {
        return installConfig.getWorkingDirectory();
    }

    @Override
    public Platform getPlatform() {
        return installConfig.getPlatform();
    }
}