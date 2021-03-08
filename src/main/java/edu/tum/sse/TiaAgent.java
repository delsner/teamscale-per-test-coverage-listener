package edu.tum.sse;

import okhttp3.HttpUrl;

public class TiaAgent {

    private final ITestwiseCoverageAgentApi api;

    /**
     * @param url URL under which the agent is reachable.
     */
    public TiaAgent(HttpUrl url) {
        api = ITestwiseCoverageAgentApi.createService(url);
    }

    /**
     * Use this when you only want to record test-wise coverage
     * and don't care about TIA's test selection and prioritization.
     */
    public TestRun startTestRunWithoutTestSelection() {
        return new TestRun(api);
    }


}
