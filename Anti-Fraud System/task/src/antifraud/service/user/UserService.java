package antifraud.service.user;

import antifraud.entity.User;
import antifraud.entity.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {
    ResponseEntity<?> register(User user);
    ResponseEntity<List<?>> getListOfUsers();
    ResponseEntity<Map<String, String>> delete(String username);
    ResponseEntity<UserResponse> changeRole(Map<String, String> data);
    ResponseEntity<?> userLockOperation(Map<String, String> data);
}
