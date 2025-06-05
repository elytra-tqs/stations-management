package elytra.stations_management;

import elytra.stations_management.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("mysql-test")
@ContextConfiguration(classes = TestContainerConfig.class)
class TestContainerExample {

    @Autowired
    private MySQLContainer<?> mysqlContainer;

    @Test
    void testMySQLContainerIsRunning() {
        assertTrue(mysqlContainer.isRunning(), "MySQL container should be running");
        assertTrue(mysqlContainer.getJdbcUrl().contains("mysql"), "JDBC URL should contain mysql");
    }
}