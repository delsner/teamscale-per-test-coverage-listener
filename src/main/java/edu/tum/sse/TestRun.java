package edu.tum.sse;

public class TestRun {

    private final ITestwiseCoverageAgentApi api;

    TestRun(ITestwiseCoverageAgentApi api) {
        this.api = api;
    }

    /**
     * Informs the testwise coverage agent that a new test is about to start.
     *
     * @throws AgentHttpRequestFailedException if communicating with the agent fails or in case of internal errors. In
     *                                         this case, the agent probably doesn't know that this test case was
     *                                         started, so its coverage is lost. This method already retries the request
     *                                         once, so this is likely a terminal failure. The caller should log this
     *                                         problem appropriately. Coverage for subsequent test cases could, however,
     *                                         potentially still be recorded. Thus, the caller should continue with test
     *                                         execution and continue informing the coverage agent about further test
     *                                         start and end events.
     */
    public RunningTest startTest(String uniformPath) throws AgentHttpRequestFailedException {
        AgentCommunicationUtils.handleRequestError(() -> api.testStarted(uniformPath),
                "Failed to start coverage recording for test case " + uniformPath);
        return new RunningTest(uniformPath, api);
    }

    /**
     * Represents the result of running a single test.
     */
    public static class TestResultWithMessage {

        /**
         * Whether the test succeeded or failed.
         */
        public final ETestExecutionResult result;

        /**
         * An optional message, e.g. a stack trace in case of test failures.
         */
        public final String message;

        public TestResultWithMessage(ETestExecutionResult result, String message) {
            this.result = result;
            this.message = message;
        }
    }

}
