package com.github.eirslett.maven.plugins.frontend.lib;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class ElmInstaller {
    public static final String INSTALL_PATH = "/node/elm";

    public static final String DEFAULT_ELM_DOWNLOAD_ROOT =
        "https://github.com/elm/compiler/releases/download/";

    private static final Object LOCK = new Object();

    private static final String ELM_ROOT_DIRECTORY = "dist";

    private String elmVersion, elmDownloadRoot, userName, password;

    private final Logger logger;

    private final InstallConfig config;

    private final ArchiveExtractor archiveExtractor;

    private final FileDownloader fileDownloader;

    ElmInstaller(InstallConfig config, ArchiveExtractor archiveExtractor, FileDownloader fileDownloader) {
        logger = LoggerFactory.getLogger(getClass());
        this.config = config;
        this.archiveExtractor = archiveExtractor;
        this.fileDownloader = fileDownloader;
    }

        public ElmInstaller setElmVersion(String elmVersion) {
        this.elmVersion = elmVersion;
        return this;
    }

    public ElmInstaller setElmDownloadRoot(String elmDownloadRoot) {
        this.elmDownloadRoot = elmDownloadRoot;
        return this;
    }

    public ElmInstaller setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public ElmInstaller setPassword(String password) {
        this.password = password;
        return this;
    }

    public void install() throws InstallationException {
        // use static lock object for a synchronized block
        synchronized (LOCK) {
            if (elmDownloadRoot == null || elmDownloadRoot.isEmpty()) {
                elmDownloadRoot = DEFAULT_ELM_DOWNLOAD_ROOT;
            }
            if (!elmIsAlreadyInstalled()) {
                if (!elmVersion.startsWith("v")) {
                    throw new InstallationException("Elm version has to start with prefix 'v'.");
                }
                installElm();
            }
        }
    }

    private boolean elmIsAlreadyInstalled() {
        try {
            ElmExecutorConfig executorConfig = new InstallElmExecutorConfig(config);
            File nodeFile = executorConfig.getElmPath();
            if (nodeFile.exists()) {
                final String version =
                    new ElmExecutor(executorConfig, Arrays.asList("--version"), null).executeAndGetResult(logger).trim();

                if (version.equals(elmVersion.replaceFirst("^v", ""))) {
                    logger.info("Elm {} is already installed.", version);
                    return true;
                } else {
                    logger.info("Yarn {} was installed, but we need version {}", version, elmVersion);
                    return false;
                }
            } else {
                return false;
            }
        } catch (ProcessExecutionException e) {
            return false;
        }
    }

    private void installElm() throws InstallationException {
        try {
            logger.info("Installing Elm version {}", elmVersion);
            String downloadUrl = elmDownloadRoot + elmVersion;
            String extension = "tar.gz";
            String fileending = "/elm-" + elmVersion + "." + extension;

            downloadUrl += fileending;

            CacheDescriptor cacheDescriptor = new CacheDescriptor("elm", elmVersion, extension);

            File archive = config.getCacheResolver().resolve(cacheDescriptor);

            downloadFileIfMissing(downloadUrl, archive, userName, password);

            File installDirectory = getInstallDirectory();

            // We need to delete the existing elm directory first so we clean out any old files, and
            // so we can rename the package directory below.
            try {
                if (installDirectory.isDirectory()) {
                    FileUtils.deleteDirectory(installDirectory);
                }
            } catch (IOException e) {
                logger.warn("Failed to delete existing Yarn installation.");
            }

            try {
                extractFile(archive, installDirectory);
            } catch (ArchiveExtractionException e) {
                if (e.getCause() instanceof EOFException) {
                    // https://github.com/eirslett/frontend-maven-plugin/issues/794
                    // The downloading was probably interrupted and archive file is incomplete:
                    // delete it to retry from scratch
                    this.logger.error("The archive file {} is corrupted and will be deleted. "
                            + "Please try the build again.", archive.getPath());
                    archive.delete();
                    if (installDirectory.exists()) {
                        FileUtils.deleteDirectory(installDirectory);
                    }
                }

                throw e;
            }

            ensureCorrectYarnRootDirectory(installDirectory, elmVersion);

            logger.info("Installed Yarn locally.");
        } catch (DownloadException e) {
            throw new InstallationException("Could not download Yarn", e);
        } catch (ArchiveExtractionException | IOException e) {
            throw new InstallationException("Could not extract the Yarn archive", e);
        }
    }

    private File getInstallDirectory() {
        File installDirectory = new File(config.getInstallDirectory(), INSTALL_PATH);
        if (!installDirectory.exists()) {
            logger.debug("Creating install directory {}", installDirectory);
            installDirectory.mkdirs();
        }
        return installDirectory;
    }

    private void extractFile(File archive, File destinationDirectory) throws ArchiveExtractionException {
        logger.info("Unpacking {} into {}", archive, destinationDirectory);
        archiveExtractor.extract(archive.getPath(), destinationDirectory.getPath());
    }

    private void ensureCorrectYarnRootDirectory(File installDirectory, String yarnVersion) throws IOException {
        File yarnRootDirectory = new File(installDirectory, ELM_ROOT_DIRECTORY);
        if (!yarnRootDirectory.exists()) {
            logger.debug("Yarn root directory not found, checking for yarn-{}", yarnVersion);
            // Handle renaming Yarn 1.X root to YARN_ROOT_DIRECTORY
            File yarnOneXDirectory = new File(installDirectory, "yarn-" + yarnVersion);
            if (yarnOneXDirectory.isDirectory()) {
                if (!yarnOneXDirectory.renameTo(yarnRootDirectory)) {
                    throw new IOException("Could not rename versioned yarn root directory to " + ELM_ROOT_DIRECTORY);
                }
            } else {
                throw new FileNotFoundException("Could not find yarn distribution directory during extract");
            }
        }
    }

    private void downloadFileIfMissing(String downloadUrl, File destination, String userName, String password)
        throws DownloadException {
        if (!destination.exists()) {
            downloadFile(downloadUrl, destination, userName, password);
        }
    }

    private void downloadFile(String downloadUrl, File destination, String userName, String password)
        throws DownloadException {
        logger.info("Downloading {} to {}", downloadUrl, destination);
        fileDownloader.download(downloadUrl, destination.getPath(), userName, password);
    }
}
