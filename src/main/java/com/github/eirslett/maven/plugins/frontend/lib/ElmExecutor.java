package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElmExecutor {
    private final ProcessExecutor executor;

    public ElmExecutor(ElmExecutorConfig config, List<String> arguments,
                        Map<String, String> additionalEnvironment) {
        final String elm = config.getElmPath().getAbsolutePath();
        List<String> localPaths = new ArrayList<>();
        localPaths.add(config.getElmPath().getParent());
        localPaths.add(config.getNodePath().getParent());
        executor = new ProcessExecutor(config.getWorkingDirectory(), localPaths,
                Utils.prepend(elm, arguments), config.getPlatform(), additionalEnvironment);
    }

    public String executeAndGetResult(final Logger logger) throws ProcessExecutionException {
        return executor.executeAndGetResult(logger);
    }

    public int executeAndRedirectOutput(final Logger logger) throws ProcessExecutionException {
        return executor.executeAndRedirectOutput(logger);
    }
}
