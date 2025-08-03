package farmsystem.union.unipath.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerificationRequestDTO {

    @NotBlank(message="이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message="이메일 코드는 필수 입력 항목입니다.")
    private String emailCode;
}
