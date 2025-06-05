package elytra.stations_management.services;

import elytra.stations_management.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByUsername(username);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User registerUser(User user) {
        // Check if username already exists
        if (repository.existsByUsername(user.getUsername())) {
            throw new UserException("Username already exists: " + user.getUsername());
        }
        // Check if email already exists
        if (repository.existsByEmail(user.getEmail())) {
            throw new UserException("Email already exists: " + user.getEmail());
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getUserType() != null) {
            existingUser.setUserType(user.getUserType());
        }
        
        return repository.save(existingUser);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
    
    public User getUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
} 