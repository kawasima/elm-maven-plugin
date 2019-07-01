package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.eirslett.maven.plugins.frontend.lib.Utils.implode;

public interface ElmRunner extends NodeTaskRunner {
}

final class DefaultElmRunner implements ElmRunner {
    private static final String DS = "//";

    private static final String AT = "@";

    private static final String TASK_NAME = "elm";

    private final Logger logger;

    private final String taskName;

    private final ArgumentsParser argumentsParser;

    private final ElmExecutorConfig config;


    DefaultElmRunner(ElmExecutorConfig config) {
        logger = LoggerFactory.getLogger(getClass());
        List<String> additionalArguments = Collections.emptyList();
        this.argumentsParser = new ArgumentsParser(additionalArguments);
        this.taskName = TASK_NAME;
        this.config = config;
    }

    @Override
    public void execute(String args, Map<String, String> environment) throws TaskRunnerException {
        final List<String> arguments = getArguments(args);
        logger.info("Running " + taskToString(taskName, arguments) + " in " + config.getWorkingDirectory());

        try {
            final int result =
                    new ElmExecutor(config, arguments, environment).executeAndRedirectOutput(logger);
            if (result != 0) {
                throw new TaskRunnerException(
                        taskToString(taskName, arguments) + " failed. (error code " + result + ")");
            }
        } catch (ProcessExecutionException e) {
            throw new TaskRunnerException(taskToString(taskName, arguments) + " failed.", e);
        }

    }

    private List<String> getArguments(String args) {
        return argumentsParser.parse(args);
    }

    private static String taskToString(String taskName, List<String> arguments) {
        List<String> clonedArguments = new ArrayList<>(arguments);
        for (int i = 0; i < clonedArguments.size(); i++) {
            final String s = clonedArguments.get(i);
            final boolean maskMavenProxyPassword = s.contains("proxy=");
            if (maskMavenProxyPassword) {
                final String bestEffortMaskedPassword = maskPassword(s);
                clonedArguments.set(i, bestEffortMaskedPassword);
            }
        }
        return "'" + taskName + " " + implode(" ", clonedArguments) + "'";
    }

    private static String maskPassword(String proxyString) {
        String retVal = proxyString;
        if (proxyString != null && !"".equals(proxyString.trim())) {
            boolean hasSchemeDefined = proxyString.contains("http:") || proxyString.contains("https:");
            boolean hasProtocolDefined = proxyString.contains(DS);
            boolean hasAtCharacterDefined = proxyString.contains(AT);
            if (hasSchemeDefined && hasProtocolDefined && hasAtCharacterDefined) {
                final int firstDoubleSlashIndex = proxyString.indexOf(DS);
                final int lastAtCharIndex = proxyString.lastIndexOf(AT);
                boolean hasPossibleURIUserInfo = firstDoubleSlashIndex < lastAtCharIndex;
                if (hasPossibleURIUserInfo) {
                    final String userInfo =
                            proxyString.substring(firstDoubleSlashIndex + DS.length(), lastAtCharIndex);
                    final String[] userParts = userInfo.split(":");
                    if (userParts.length > 0) {
                        final int startOfUserNameIndex = firstDoubleSlashIndex + DS.length();
                        final int firstColonInUsernameOrEndOfUserNameIndex =
                                startOfUserNameIndex + userParts[0].length();
                        final String leftPart =
                                proxyString.substring(0, firstColonInUsernameOrEndOfUserNameIndex);
                        final String rightPart = proxyString.substring(lastAtCharIndex);
                        retVal = leftPart + ":***" + rightPart;
                    }
                }
            }
        }
        return retVal;
    }
}
