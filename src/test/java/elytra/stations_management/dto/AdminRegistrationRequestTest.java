package elytra.stations_management.dto;

import elytra.stations_management.models.Admin;
import elytra.stations_management.models.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdminRegistrationRequestTest {

    @Test
    void testGettersAndSetters() {
        // Given
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        Admin admin = Admin.builder()
                .id(1L)
                .build();
        User user = User.builder()
                .id(1L)
                .username("admin1")
                .email("admin@example.com")
                .build();

        // When
        request.setAdmin(admin);
        request.setUser(user);

        // Then
        assertThat(request.getAdmin())
                .isNotNull()
                .isEqualTo(admin)
                .extracting(Admin::getId)
                .isEqualTo(1L);

        assertThat(request.getUser())
                .isNotNull()
                .isEqualTo(user)
                .extracting(User::getUsername, User::getEmail)
                .containsExactly("admin1", "admin@example.com");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Admin admin = Admin.builder().id(1L).build();
        User user = User.builder().id(1L).username("admin1").build();

        AdminRegistrationRequest request1 = new AdminRegistrationRequest();
        request1.setAdmin(admin);
        request1.setUser(user);

        AdminRegistrationRequest request2 = new AdminRegistrationRequest();
        request2.setAdmin(admin);
        request2.setUser(user);

        AdminRegistrationRequest request3 = new AdminRegistrationRequest();
        request3.setAdmin(Admin.builder().id(2L).build());
        request3.setUser(user);

        // Then
        assertThat(request1)
                .isEqualTo(request2)
                .hasSameHashCodeAs(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(null)
                .isNotEqualTo(new Object());

        assertThat(request1.hashCode()).isNotEqualTo(request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        Admin admin = Admin.builder().id(1L).build();
        User user = User.builder().username("admin1").build();
        request.setAdmin(admin);
        request.setUser(user);

        // When
        String toString = request.toString();

        // Then
        assertThat(toString)
                .isNotNull()
                .contains("AdminRegistrationRequest")
                .contains("admin=")
                .contains("user=");
    }

    @Test
    void testCanEqual() {
        // Given
        AdminRegistrationRequest request1 = new AdminRegistrationRequest();
        AdminRegistrationRequest request2 = new AdminRegistrationRequest();
        
        // Then
        assertThat(request1.canEqual(request2)).isTrue();
        assertThat(request1.canEqual(new Object())).isFalse();
    }

    @Test
    void testEmptyRequest() {
        // Given
        AdminRegistrationRequest request = new AdminRegistrationRequest();

        // Then
        assertThat(request.getAdmin()).isNull();
        assertThat(request.getUser()).isNull();
    }

    @Test
    void testWithNullFields() {
        // Given
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        request.setAdmin(null);
        request.setUser(null);

        // Then
        assertThat(request)
                .hasFieldOrPropertyWithValue("admin", null)
                .hasFieldOrPropertyWithValue("user", null);
    }
}