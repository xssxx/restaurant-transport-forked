package ku.cs.restaurant.dto.order;

import ku.cs.restaurant.entity.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String phone;
    private String role;

    public static UserResponse from(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole());
        return res;
    }
}
