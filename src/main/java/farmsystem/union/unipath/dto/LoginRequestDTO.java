package farmsystem.union.unipath.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDTO {

    @NotBlank(message="학번은 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
