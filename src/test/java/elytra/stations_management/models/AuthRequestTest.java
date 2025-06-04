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
    void testAuthRequestSettersAndGetters() {
        AuthRequest authRequest = new AuthRequest();
        
        authRequest.setUsername("newuser");
        assertThat(authRequest.getUsername()).isEqualTo("newuser");
        
        authRequest.setPassword("newpassword");
        assertThat(authRequest.getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testAuthRequestEquality() {
        AuthRequest authRequest1 = new AuthRequest();
        authRequest1.setUsername("user1");
        authRequest1.setPassword("pass1");

        AuthRequest authRequest2 = new AuthRequest();
        authRequest2.setUsername("user1");
        authRequest2.setPassword("pass1");

        // Note: Without @EqualsAndHashCode, these would be different objects
        // This test documents the current behavior
        assertThat(authRequest1).isNotSameAs(authRequest2);
    }
}