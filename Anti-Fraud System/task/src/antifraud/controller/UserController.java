package antifraud.controller;

import antifraud.entity.User;
import antifraud.entity.response.UserResponse;
import antifraud.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

    @GetMapping("/list")
    public ResponseEntity<List<?>> getListOfUsers() {
        return userService.getListOfUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String username) {
        return userService.delete(username);
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponse> changeRole(@RequestBody Map<String, String> data) {
        return userService.changeRole(data);
    }

    @PutMapping("/access")
    public ResponseEntity<?> userLockOperation(@RequestBody Map<String, String> data) {
        return userService.userLockOperation(data);
    }



}
