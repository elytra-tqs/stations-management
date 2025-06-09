package elytra.stations_management.cucumber;

import io.cucumber.core.cli.Main;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunCucumberFeatureTest {

    @Test
    public void runCucumberFeatures() {
        String[] args = {
            "--glue", "elytra.stations_management.cucumber.steps",
            "--glue", "elytra.stations_management.cucumber",
            "--plugin", "pretty",
            "--plugin", "html:target/cucumber-html-reports",
            "--plugin", "json:target/cucumber-json-reports/cucumber.json",
            "classpath:features"
        };
        
        byte exitCode = Main.run(args, Thread.currentThread().getContextClassLoader());
        assertEquals(0, exitCode, "Cucumber tests failed!");
    }
}