package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.User;
import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private final Long id;
    private final String userId;
    private final String name;
    private final String email;

    public LoginResponseDTO(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}