package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();
    }

    @Test
    void testUserCreation() {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getUserType()).isEqualTo(User.UserType.EV_DRIVER);
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertThat(emptyUser).isNotNull();
        assertThat(emptyUser.getId()).isNull();
        assertThat(emptyUser.getUsername()).isNull();
        assertThat(emptyUser.getPassword()).isNull();
        assertThat(emptyUser.getEmail()).isNull();
        assertThat(emptyUser.getFirstName()).isNull();
        assertThat(emptyUser.getLastName()).isNull();
        assertThat(emptyUser.getUserType()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        User fullUser = new User(2L, "admin", "adminPass", "admin@example.com", 
                                 "Admin", "User", User.UserType.ADMIN);
        
        assertThat(fullUser.getId()).isEqualTo(2L);
        assertThat(fullUser.getUsername()).isEqualTo("admin");
        assertThat(fullUser.getPassword()).isEqualTo("adminPass");
        assertThat(fullUser.getEmail()).isEqualTo("admin@example.com");
        assertThat(fullUser.getFirstName()).isEqualTo("Admin");
        assertThat(fullUser.getLastName()).isEqualTo("User");
        assertThat(fullUser.getUserType()).isEqualTo(User.UserType.ADMIN);
    }

    @Test
    void testBuilder() {
        User builtUser = User.builder()
                .id(3L)
                .username("operator")
                .password("operatorPass")
                .email("operator@example.com")
                .firstName("Station")
                .lastName("Operator")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        assertThat(builtUser.getId()).isEqualTo(3L);
        assertThat(builtUser.getUsername()).isEqualTo("operator");
        assertThat(builtUser.getPassword()).isEqualTo("operatorPass");
        assertThat(builtUser.getEmail()).isEqualTo("operator@example.com");
        assertThat(builtUser.getFirstName()).isEqualTo("Station");
        assertThat(builtUser.getLastName()).isEqualTo("Operator");
        assertThat(builtUser.getUserType()).isEqualTo(User.UserType.STATION_OPERATOR);
    }

    @Test
    void testSettersAndGetters() {
        User testUser = new User();
        
        testUser.setId(4L);
        testUser.setUsername("newuser");
        testUser.setPassword("newpass");
        testUser.setEmail("new@example.com");
        testUser.setFirstName("New");
        testUser.setLastName("User");
        testUser.setUserType(User.UserType.EV_DRIVER);

        assertThat(testUser.getId()).isEqualTo(4L);
        assertThat(testUser.getUsername()).isEqualTo("newuser");
        assertThat(testUser.getPassword()).isEqualTo("newpass");
        assertThat(testUser.getEmail()).isEqualTo("new@example.com");
        assertThat(testUser.getFirstName()).isEqualTo("New");
        assertThat(testUser.getLastName()).isEqualTo("User");
        assertThat(testUser.getUserType()).isEqualTo(User.UserType.EV_DRIVER);
    }

    @Test
    void testGetRoles_WithEvDriver() {
        user.setUserType(User.UserType.EV_DRIVER);
        assertThat(user.getRoles()).isEqualTo("ROLE_EV_DRIVER");
    }

    @Test
    void testGetRoles_WithStationOperator() {
        user.setUserType(User.UserType.STATION_OPERATOR);
        assertThat(user.getRoles()).isEqualTo("ROLE_STATION_OPERATOR");
    }

    @Test
    void testGetRoles_WithAdmin() {
        user.setUserType(User.UserType.ADMIN);
        assertThat(user.getRoles()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testGetRoles_WithNullUserType() {
        user.setUserType(null);
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
    }

    @Test
    void testToString() {
        String userString = user.toString();
        assertThat(userString).contains(
                "User",
                "id=1",
                "username=testuser",
                "email=test@example.com",
                "firstName=John",
                "lastName=Doe",
                "userType=EV_DRIVER"
        );
    }

    @Test
    void testEquals() {
        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        User user3 = User.builder()
                .id(2L)
                .username("differentuser")
                .password("differentPass")
                .email("different@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .userType(User.UserType.ADMIN)
                .build();

        assertThat(user1)
                .isEqualTo(user2)
                .isNotEqualTo(user3)
                .isNotEqualTo(null)
                .isNotEqualTo("not a user");
    }

    @Test
    void testHashCode() {
        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        assertThat(user1).hasSameHashCodeAs(user2);
    }

    @Test
    void testBuilderWithPartialData() {
        User partialUser = User.builder()
                .username("partial")
                .email("partial@example.com")
                .build();

        assertThat(partialUser.getId()).isNull();
        assertThat(partialUser.getUsername()).isEqualTo("partial");
        assertThat(partialUser.getPassword()).isNull();
        assertThat(partialUser.getEmail()).isEqualTo("partial@example.com");
        assertThat(partialUser.getFirstName()).isNull();
        assertThat(partialUser.getLastName()).isNull();
        assertThat(partialUser.getUserType()).isNull();
    }

    @Test
    void testSetAndGetNullValues() {
        User testUser = new User();
        
        testUser.setUsername(null);
        testUser.setPassword(null);
        testUser.setEmail(null);
        testUser.setFirstName(null);
        testUser.setLastName(null);
        testUser.setUserType(null);

        assertThat(testUser.getUsername()).isNull();
        assertThat(testUser.getPassword()).isNull();
        assertThat(testUser.getEmail()).isNull();
        assertThat(testUser.getFirstName()).isNull();
        assertThat(testUser.getLastName()).isNull();
        assertThat(testUser.getUserType()).isNull();
    }

    @Test
    void testUserTypeEnum() {
        assertThat(User.UserType.values()).containsExactly(
            User.UserType.EV_DRIVER,
            User.UserType.STATION_OPERATOR,
            User.UserType.ADMIN
        );
        
        assertThat(User.UserType.valueOf("EV_DRIVER")).isEqualTo(User.UserType.EV_DRIVER);
        assertThat(User.UserType.valueOf("STATION_OPERATOR")).isEqualTo(User.UserType.STATION_OPERATOR);
        assertThat(User.UserType.valueOf("ADMIN")).isEqualTo(User.UserType.ADMIN);
    }
}