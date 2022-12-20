package antifraud.service.user;

import antifraud.entity.User;
import antifraud.entity.Values;
import antifraud.entity.enums.Role;
import antifraud.entity.response.UserResponse;
import antifraud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(User user) {
        User responseUser = userRepository.findByUsername(user.getUsername().trim());
        if (responseUser == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (!Values.isAdminRegistered()) {
                user.setRole(Role.ADMINISTRATOR);
                user.setNonBlocked(true);
                Values.setAdminRegistered(true);
            } else {
                user.setRole(Role.MERCHANT);
                user.setNonBlocked(false);
            }
            userRepository.save(user);
            return new ResponseEntity<>(new UserResponse(user), HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    @Override
    @Transactional
    public ResponseEntity<List<?>> getListOfUsers() {
        Iterable<User> userIterable = userRepository.findAll();
        List<UserResponse> userList = new ArrayList<>();
        for (User user : userIterable) userList.add(new UserResponse(user));
        userList.sort(Comparator.comparing(UserResponse::getId));
        return ResponseEntity.ok(userList);
    }

    @Override
    public ResponseEntity<Map<String, String>> delete(String username) {
        User user = userRepository.findByUsername(username.trim());
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.ok(Map.of("username", username, "status", "Deleted successfully!"));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    @Transactional
    public ResponseEntity<UserResponse> changeRole(Map<String, String> data) {
        if (!data.containsKey("role") || data.get("role").isBlank() || (!data.get("role").equals("SUPPORT") && !data.get("role").equals("MERCHANT")) || !data.containsKey("username") || data.get("username").isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String username = data.get("username").trim();
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String role = data.get("role").toUpperCase();
        if (user.getRole() == Role.ADMINISTRATOR || user.getRole().name().equals(role))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        user.setRole(Stream.of(Role.values())
                .filter(c -> c.name().equals(role))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new));
        userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @Override
    public ResponseEntity<?> userLockOperation(Map<String, String> data) {
        String username = data.get("username");
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (user.getRole() == Role.ADMINISTRATOR)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String operation = data.get("operation").toLowerCase() + "ed";
        if (!user.isAccountNonLocked() && operation.equals("locked") || user.isAccountNonLocked() && operation.equals("unlocked"))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        user.setNonBlocked(!operation.equals("locked"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("status", String.format("User %s %s!", username, operation)));
    }
}
