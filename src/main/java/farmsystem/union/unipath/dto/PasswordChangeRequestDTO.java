package farmsystem.union.unipath.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDTO {
    private String currentPassword;
    private String newPassword;
}