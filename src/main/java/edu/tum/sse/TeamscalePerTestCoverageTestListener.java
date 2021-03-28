package edu.tum.sse;

import okhttp3.HttpUrl;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TeamscalePerTestCoverageTestListener extends RunListener implements TestExecutionListener {

    private final TestRun testRun;
    private RunningTest runningTest;

    public TeamscalePerTestCoverageTestListener() {
        System.out.println("Starting teamscale per-test coverage listener for new test...");
        String agentUrl = System.getProperty("tia.agent");
        if (agentUrl == null) {
            agentUrl = System.getenv("TIA_AGENT");
        }
        if (agentUrl == null) {
            throw new RunListenerConfigurationException(
                    "You did not provide the URL of a Teamscale JaCoCo agent that will record test-wise coverage." +
                            " You can configure the URL either as a system property with -Dtia.agent=URL" +
                            " or as an environment variable with TIA_AGENT=URL.");
        }

        TiaAgent agent = new TiaAgent(HttpUrl.get(agentUrl));
        testRun = agent.startTestRunWithoutTestSelection();
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        Description description = convertTestIdentifierToDescription(testIdentifier);
        if (description != null) {
            testStarted(description);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        Description description = convertTestIdentifierToDescription(testIdentifier);
        if (description != null) {
            switch (testExecutionResult.getStatus()) {
                case FAILED:
                    testFailure(
                            new Failure(description,
                                    testExecutionResult
                                            .getThrowable()
                                            .orElseThrow(
                                                    () -> new RunListenerConversionException("Failed to convert thrown exception for test failure."))
                            ));
                default:
                    testFinished(description);
            }
        }
    }

    /**
     * We must convert the JUnit5 {@link TestIdentifier} to a JUnit4-compatible description format {@link Description}.
     * This method will return `null` in case the test identifier is neither a test suite nor a test method.
     */
    private Description convertTestIdentifierToDescription(TestIdentifier testIdentifier) {
        // [engine:junit-vintage/jupiter] containers will not have a parent and are excluded
        if (!testIdentifier.getParentId().isPresent()) {
            return null;
        }

        if (!testIdentifier.getSource().isPresent()) {
            return null;
        }
        TestSource source = testIdentifier.getSource().get();

        // we only count containers as test suites that are classes
        // parameterized test methods are excluded by returning null here
        if (!(testIdentifier.isTest()) && !(source instanceof ClassSource)) {
            return null;
        }

        // a test that does not have a method source is ignored
        if (testIdentifier.isTest() && !(source instanceof MethodSource)) {
            return null;
        }

        if (testIdentifier.isTest()) {
            MethodSource methodSource = (MethodSource) source;
            String className = methodSource.getClassName();
            return Description.createTestDescription(className, testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
        }
        return Description.createSuiteDescription(testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
    }

    /**
     * We mustn't throw exceptions out of the {@link RunListener} interface methods or Maven will treat the test as
     * failed. And we don't have access to the Maven logger, so we just log to stderr.
     */
    private void handleErrors(Action action) {
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Encountered an error while recording test-wise coverage:");
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void testStarted(Description description) {
        handleErrors(() -> runningTest = testRun.startTest(getUniformPath(description)));
    }

    private String getUniformPath(Description description) {
        String uniformPath = description.getClassName().replace('.', '/');
        if (description.getMethodName() != null) {
            uniformPath += "/" + description.getMethodName();
        }
        return uniformPath;
    }

    @Override
    public void testFinished(Description description) {
        handleErrors(() -> {
            if (runningTest != null) {
                runningTest.endTest(new TestRun.TestResultWithMessage(ETestExecutionResult.PASSED, null));
                runningTest = null;
            }
        });
    }

    @Override
    public void testFailure(Failure failure) {
        handleErrors(() -> {
            if (runningTest != null) {
                runningTest.endTest(
                        new TestRun.TestResultWithMessage(ETestExecutionResult.FAILURE, failure.getTrace()));
                runningTest = null;
            }
        });
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testFailure(failure);
    }

    @FunctionalInterface
    private interface Action {
        /**
         * Runs the action, throwing exceptions if it fails.
         */
        void run() throws Exception;
    }

    private static class RunListenerConfigurationException extends RuntimeException {
        public RunListenerConfigurationException(String message) {
            super(message);
        }
    }

    private static class RunListenerConversionException extends RuntimeException {
        public RunListenerConversionException(String message) {
            super(message);
        }
    }
}
