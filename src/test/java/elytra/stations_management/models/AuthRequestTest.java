package elytra.stations_management.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthRequestTest {

    @Test
    void testAuthRequestCreation() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");

        assertThat(authRequest.getUsername()).isEqualTo("testuser");
        assertThat(authRequest.getPassword()).isEqualTo("testpassword");
    }

    @Test
    void testAuthRequestDefaultConstructor() {
        AuthRequest authRequest = new AuthRequest();

        assertThat(authRequest.getUsername()).isNull();
        assertThat(authRequest.getPassword()).isNull();
    }

    @Test
    void testAuthRequestAllArgsConstructor() {
        AuthRequest authRequest = new AuthRequest("testuser", "testpassword");

        assertThat(authRequest.getUsername()).isEqualTo("testuser");
        assertThat(authRequest.getPassword()).isEqualTo("testpassword");
    }

    @Test
    void testAuthRequestSettersAndGetters() {
        AuthRequest authRequest = new AuthRequest();
        
        authRequest.setUsername("newuser");
        assertThat(authRequest.getUsername()).isEqualTo("newuser");
        
        authRequest.setPassword("newpassword");
        assertThat(authRequest.getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testEquals() {
        AuthRequest authRequest1 = new AuthRequest("user1", "pass1");
        AuthRequest authRequest2 = new AuthRequest("user1", "pass1");
        AuthRequest authRequest3 = new AuthRequest("user2", "pass1");
        AuthRequest authRequest4 = new AuthRequest("user1", "pass2");

        // Test reflexivity
        assertThat(authRequest1).isEqualTo(authRequest1);

        // Test symmetry
        assertThat(authRequest1).isEqualTo(authRequest2);
        assertThat(authRequest2).isEqualTo(authRequest1);

        // Test different username
        assertThat(authRequest1).isNotEqualTo(authRequest3);

        // Test different password
        assertThat(authRequest1).isNotEqualTo(authRequest4);

        // Test null
        assertThat(authRequest1).isNotEqualTo(null);

        // Test different type
        assertThat(authRequest1).isNotEqualTo("not an AuthRequest");
    }

    @Test
    void testEqualsWithNullFields() {
        AuthRequest authRequest1 = new AuthRequest(null, "pass1");
        AuthRequest authRequest2 = new AuthRequest(null, "pass1");
        AuthRequest authRequest3 = new AuthRequest("user1", null);
        AuthRequest authRequest4 = new AuthRequest("user1", null);
        AuthRequest authRequest5 = new AuthRequest(null, null);
        AuthRequest authRequest6 = new AuthRequest(null, null);

        assertThat(authRequest1).isEqualTo(authRequest2);
        assertThat(authRequest3).isEqualTo(authRequest4);
        assertThat(authRequest5).isEqualTo(authRequest6);
        assertThat(authRequest1).isNotEqualTo(authRequest3);
    }

    @Test
    void testHashCode() {
        AuthRequest authRequest1 = new AuthRequest("user1", "pass1");
        AuthRequest authRequest2 = new AuthRequest("user1", "pass1");
        AuthRequest authRequest3 = new AuthRequest("user2", "pass1");

        // Equal objects must have equal hash codes
        assertThat(authRequest1).hasSameHashCodeAs(authRequest2);

        // Different objects should (likely) have different hash codes
        assertThat(authRequest1.hashCode()).isNotEqualTo(authRequest3.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        AuthRequest authRequest1 = new AuthRequest(null, "pass1");
        AuthRequest authRequest2 = new AuthRequest(null, "pass1");
        AuthRequest authRequest3 = new AuthRequest("user1", null);
        AuthRequest authRequest4 = new AuthRequest(null, null);

        assertThat(authRequest1).hasSameHashCodeAs(authRequest2);
        
        // Test that null fields don't cause NullPointerException
        assertThat(authRequest3.hashCode()).isNotNull();
        assertThat(authRequest4.hashCode()).isNotNull();
    }

    @Test
    void testToString() {
        AuthRequest authRequest = new AuthRequest("testuser", "testpassword");
        String toString = authRequest.toString();

        assertThat(toString).contains("AuthRequest");
        assertThat(toString).contains("username=testuser");
        assertThat(toString).contains("password=testpassword");
    }

    @Test
    void testToStringWithNullFields() {
        AuthRequest authRequest = new AuthRequest(null, null);
        String toString = authRequest.toString();

        assertThat(toString).contains("AuthRequest");
        assertThat(toString).contains("username=null");
        assertThat(toString).contains("password=null");
    }

    @Test
    void testCanEqual() {
        AuthRequest authRequest1 = new AuthRequest("user1", "pass1");
        AuthRequest authRequest2 = new AuthRequest("user2", "pass2");

        // Test canEqual with same type
        assertThat(authRequest1.canEqual(authRequest2)).isTrue();
        assertThat(authRequest2.canEqual(authRequest1)).isTrue();

        // Test canEqual with different type
        assertThat(authRequest1.canEqual("not an AuthRequest")).isFalse();
        assertThat(authRequest1.canEqual(null)).isFalse();
    }
}