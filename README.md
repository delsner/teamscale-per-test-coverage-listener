# Teamscale Per-Test Coverage JUnit Test Listener

A simple JUnit test listener that interacts with the Teamscale Jacoco Agent, all credits go to [CQSE/Teamscale](https://github.com/cqse/teamscale-jacoco-agent/tree/master/tia-client).

## Setup

Install this package to your local Maven repository:

```shell script
$ mvn clean install
```

Download the most [recent release of the Teamscale Jacoco Agent](https://github.com/cqse/teamscale-jacoco-agent/releases) and unzip to your desired `JACOCO_AGENT_LOCATION`.
 
## Collecting testwise coverage

### Add JUnit test listener to Maven Surefire plugin

```xml
<!-- ... -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M5</version>
    <configuration>
        <properties>
            <property>
                <name>listener</name>
                <value>edu.tum.sse.TeamscalePerTestCoverageTestListener</value>
            </property>
        </properties>
    </configuration>
</plugin>
<!-- ... ->
```

### Run the instrumented test suite

In the root of your Maven project run:

```shell script
$ mvn clean test \ 
  -DargLine="-javaagent:JACOCO_AGENT_LOCATION/lib/teamscale-jacoco-agent.jar=includes=*com.comp.*,mode=testwise,interval=0,http-server-port=8123,tia-mode=exec-file" \
  -DfailIfNoTests=false \
  -fn \
  -Dtia.agent=http://localhost:8123
```

### Converting the coverage into JSON format

Still in the root of your Maven project run:

```shell script
$ JACOCO_AGENT_LOCATION/teamscale-jacoco-agent/bin/convert --testwise-coverage \
  -i JACOCO_AGENT_LOCATION/coverage \ 
  -o JACOCO_AGENT_LOCATION/output \
  -c . 
```
