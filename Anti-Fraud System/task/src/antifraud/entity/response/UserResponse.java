package antifraud.entity.response;

import antifraud.entity.User;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "name",
        "username",
        "role"
})
public final class UserResponse {
    private final Long id;
    private final String name;
    private final String username;
    private final String role;

    public UserResponse(User user) {
        id = user.getId();
        name = user.getName();
        username = user.getUsername();
        role = user.getRole().name();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
