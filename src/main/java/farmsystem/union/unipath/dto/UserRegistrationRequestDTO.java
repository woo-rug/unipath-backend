package farmsystem.union.unipath.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRegistrationRequestDTO {

    @NotBlank(message="이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message="학번은 필수 입력 항목입니다.")
    @Size(min=10, max=10, message="학번은 10자입니다.")
    @Pattern(regexp = "^202[0-5]\\d{6}$", message = "학번은 2020xxxxxx부터 2025xxxxxx 사이의 형태여야 합니다.")
    private String userId;

    @NotBlank(message="비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message="이메일은 필수 입력 항목입니다.")
    @Email(message="유효한 이메일 형식이 아닙니다.")
    @Pattern(regexp = ".*@dgu\\.ac\\.kr$", message = "이메일은 dgu.ac.kr 도메인이어야 합니다.")
    private String email;
}
