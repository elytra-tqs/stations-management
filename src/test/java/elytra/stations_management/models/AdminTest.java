package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminTest {

    private Admin admin;
    private User user;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("admin1")
                .password("hashedPassword")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .userType(User.UserType.ADMIN)
                .build();

        Station station1 = Station.builder()
                .id(1L)
                .name("Station 1")
                .address("123 Main St")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        Station station2 = Station.builder()
                .id(2L)
                .name("Station 2")
                .address("456 Oak Ave")
                .latitude(40.7580)
                .longitude(-73.9855)
                .build();

        stations = Arrays.asList(station1, station2);

        admin = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();
    }

    @Test
    void testAdminCreation() {
        assertThat(admin).isNotNull();
        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getUser()).isEqualTo(user);
        assertThat(admin.getStations()).hasSize(2);
        assertThat(admin.getStations()).containsExactlyElementsOf(stations);
    }

    @Test
    void testNoArgsConstructor() {
        Admin emptyAdmin = new Admin();
        assertThat(emptyAdmin).isNotNull();
        assertThat(emptyAdmin.getId()).isNull();
        assertThat(emptyAdmin.getUser()).isNull();
        assertThat(emptyAdmin.getStations()).isNotNull();
        assertThat(emptyAdmin.getStations()).isEmpty();
    }

    @Test
    void testAllArgsConstructor() {
        Admin fullAdmin = new Admin(2L, user, new ArrayList<>(stations));
        
        assertThat(fullAdmin.getId()).isEqualTo(2L);
        assertThat(fullAdmin.getUser()).isEqualTo(user);
        assertThat(fullAdmin.getStations()).hasSize(2);
    }

    @Test
    void testBuilder() {
        Admin builtAdmin = Admin.builder()
                .id(3L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        assertThat(builtAdmin.getId()).isEqualTo(3L);
        assertThat(builtAdmin.getUser()).isEqualTo(user);
        assertThat(builtAdmin.getStations()).isEmpty();
    }

    @Test
    void testBuilderDefault() {
        Admin defaultAdmin = Admin.builder()
                .id(1L)
                .user(user)
                .build();

        assertThat(defaultAdmin.getStations()).isNotNull();
        assertThat(defaultAdmin.getStations()).isEmpty();
    }

    @Test
    void testSettersAndGetters() {
        Admin testAdmin = new Admin();
        
        testAdmin.setId(4L);
        testAdmin.setUser(user);
        testAdmin.setStations(new ArrayList<>(stations));

        assertThat(testAdmin.getId()).isEqualTo(4L);
        assertThat(testAdmin.getUser()).isEqualTo(user);
        assertThat(testAdmin.getStations()).hasSize(2);
    }

    @Test
    void testToString() {
        String adminString = admin.toString();
        assertThat(adminString).contains("Admin", "id=1", "user=", "stations=");
    }

    @Test
    void testEquals() {
        Admin admin1 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        Admin admin2 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        Admin admin3 = Admin.builder()
                .id(2L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        assertThat(admin1)
                .isEqualTo(admin1)
                .isEqualTo(admin2)
                .isNotEqualTo(admin3)
                .isNotEqualTo(null)
                .isNotEqualTo("not an admin");
    }

    @Test
    void testHashCode() {
        Admin admin1 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        Admin admin2 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        assertThat(admin1).hasSameHashCodeAs(admin2);
    }

    @Test
    void testAddStation() {
        Admin testAdmin = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        Station newStation = Station.builder()
                .id(3L)
                .name("New Station")
                .address("789 New St")
                .build();

        testAdmin.getStations().add(newStation);

        assertThat(testAdmin.getStations()).hasSize(1);
        assertThat(testAdmin.getStations()).contains(newStation);
    }

    @Test
    void testRemoveStation() {
        Admin testAdmin = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        Station stationToRemove = testAdmin.getStations().get(0);
        testAdmin.getStations().remove(stationToRemove);

        assertThat(testAdmin.getStations()).hasSize(1);
        assertThat(testAdmin.getStations()).doesNotContain(stationToRemove);
    }

    @Test
    void testDifferentUsers() {
        User user2 = User.builder()
                .id(2L)
                .username("admin2")
                .email("admin2@example.com")
                .userType(User.UserType.ADMIN)
                .build();

        Admin admin1 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        Admin admin2 = Admin.builder()
                .id(1L)
                .user(user2)
                .stations(new ArrayList<>())
                .build();

        assertThat(admin1).isNotEqualTo(admin2);
    }

    @Test
    void testDifferentStations() {
        Admin admin1 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>(stations))
                .build();

        Admin admin2 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        assertThat(admin1).isNotEqualTo(admin2);
    }

    @Test
    void testCanEqual() {
        Admin admin1 = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        Admin admin2 = Admin.builder()
                .id(2L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        assertThat(admin1.canEqual(admin2)).isTrue();
        assertThat(admin1.canEqual("not an admin")).isFalse();
        assertThat(admin1.canEqual(null)).isFalse();
    }

    @Test
    void testEmptyStationsList() {
        Admin adminWithEmptyStations = Admin.builder()
                .id(1L)
                .user(user)
                .stations(new ArrayList<>())
                .build();

        assertThat(adminWithEmptyStations.getStations()).isNotNull();
        assertThat(adminWithEmptyStations.getStations()).isEmpty();
    }

    @Test
    void testNullStationsList() {
        Admin adminWithNullStations = Admin.builder()
                .id(1L)
                .user(user)
                .stations(null)
                .build();

        assertThat(adminWithNullStations.getStations()).isNull();
    }
}