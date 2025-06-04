package elytra.stations_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the SECRET field using reflection since @Value won't work without Spring context
        ReflectionTestUtils.setField(jwtService, "SECRET", SECRET);
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken("testuser");

        assertThat(token)
                .isNotNull()
                .isNotBlank()
                .matches(t -> t.split("\\.").length == 3, "JWT has 3 parts"); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken("testuser");
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("testuser");
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtService.generateToken("testuser");
        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateToken("testuser");
        Boolean isValid = jwtService.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_ForDifferentUser() {
        String token = jwtService.generateToken("testuser");
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Boolean isValid = jwtService.validateToken(token, differentUser);

        assertThat(isValid).isFalse();
    }


    @Test
    void extractUsername_ShouldThrowException_ForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }


    @Test
    void tokenGeneration_ShouldBeConsistent() throws InterruptedException {
        String token1 = jwtService.generateToken("testuser");
        Thread.sleep(1000); // Ensure different timestamps
        String token2 = jwtService.generateToken("testuser");

        // Tokens should be different (different timestamps)
        assertThat(token1).isNotEqualTo(token2);

        // But should have same username
        assertThat(jwtService.extractUsername(token1))
                .isEqualTo(jwtService.extractUsername(token2));
    }
}