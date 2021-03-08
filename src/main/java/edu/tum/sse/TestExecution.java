package edu.tum.sse;

import java.io.Serializable;

/**
 * Representation of a single test (method) execution.
 */
public class TestExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The uniform path of the test (method) that was executed. This is an absolute (i.e. hierarchical) reference which
     * identifies the test uniquely in the scope of the Teamscale project. It may (but is not required to) correspond to
     * the path of some automated test case source code known to Teamscale. If the test was parameterized, this path is
     * expected to reflect the parameter in some manner.
     */
    private String uniformPath;

    /**
     * Duration of the execution in milliseconds.
     */
    @Deprecated
    private long durationMillis;

    /**
     * Duration of the execution in seconds.
     */
    private Double duration;

    /**
     * The actual execution result state.
     */
    private ETestExecutionResult result;

    /**
     * Optional message given for test failures (normally contains a stack trace). May be {@code null}.
     */
    private String message;

    public TestExecution(String name, long durationMillis, ETestExecutionResult result) {
        this(name, durationMillis, result, null);
    }

    public TestExecution(String name, long durationMillis, ETestExecutionResult result, String message) {
        this.uniformPath = name;
        this.durationMillis = durationMillis;
        this.result = result;
        this.message = message;
    }

    /**
     * @see #durationMillis
     */
    public double getDurationSeconds() {
        if (duration != null) {
            return duration;
        } else {
            return durationMillis / 1000.0;
        }
    }

    /**
     * @see #result
     */
    public ETestExecutionResult getResult() {
        return result;
    }

    /**
     * @see #result
     */
    public void setResult(ETestExecutionResult result) {
        this.result = result;
    }

    /**
     * @see #message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @see #message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @see #uniformPath
     */
    public String getUniformPath() {
        return uniformPath;
    }

    /**
     * @see #uniformPath
     */
    public void setUniformPath(String uniformPath) {
        this.uniformPath = uniformPath;
    }

    /**
     * @see #durationMillis
     */
    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    @Override
    public String toString() {
        return "TestExecution{" +
                "uniformPath='" + uniformPath + '\'' +
                ", durationMillis=" + durationMillis +
                ", duration=" + duration +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
