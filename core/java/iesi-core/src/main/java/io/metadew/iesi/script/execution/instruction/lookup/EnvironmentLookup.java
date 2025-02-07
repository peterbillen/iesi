package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.metadata.configuration.environment.EnvironmentParameterConfiguration;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentLookup implements LookupInstruction {

    private final String ENVIRONMENT_NAME_KEY = "name";

    private final String ENVIRONMENT_PARAMETER_NAME_KEY = "parameterName";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + ENVIRONMENT_NAME_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + ENVIRONMENT_PARAMETER_NAME_KEY + ">(\\w|\\.)+)\\s*");
    private final EnvironmentParameterConfiguration environmentParameterConfiguration;

    public EnvironmentLookup() {
        environmentParameterConfiguration = new EnvironmentParameterConfiguration();}

    @Override
    public String getKeyword() {
        return "environment";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to connection lookup: {0}", parameters));
        }
        String environmentName = inputParameterMatcher.group(ENVIRONMENT_NAME_KEY);
        String environmentParameterName = inputParameterMatcher.group(ENVIRONMENT_PARAMETER_NAME_KEY);

        Optional<String> environmentParameterValue = environmentParameterConfiguration.getEnvironmentParameterValue(environmentName, environmentParameterName);

        if (!environmentParameterValue.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No environment parameter {0} is attached to environment {1}", environmentParameterName, environmentName));
        } else {
            return environmentParameterValue.get();
        }

    }
}
