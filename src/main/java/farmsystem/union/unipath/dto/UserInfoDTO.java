package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.User;
import lombok.Getter;

@Getter
public class UserInfoDTO {
    private final String name;
    private final String userId; // 학번
    private final String email;

    public UserInfoDTO(User user) {
        this.name = user.getName();
        this.userId = user.getUserId();
        this.email = user.getEmail();
    }
}