package elytra.stations_management.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"elytra.stations_management.cucumber.steps", "elytra.stations_management.cucumber"},
    plugin = {"pretty", "html:target/cucumber-reports", "json:target/cucumber-reports/Cucumber.json"},
    monochrome = true
)
public class RunCucumberTest {
    // This class will be picked up by maven surefire plugin
}