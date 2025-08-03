package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.EmailVerificationRequestDTO;
import farmsystem.union.unipath.dto.UserRegistrationRequestDTO;
import farmsystem.union.unipath.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // POST /api/users/register
    // 회원가입 정보 임시 저장 후 이메일 인증 코드 발송
    @PostMapping("/register")
    public ResponseEntity<String> tempRegister(@Valid @RequestBody UserRegistrationRequestDTO requestDTO){
        userService.tempRegister(requestDTO);
        return ResponseEntity.ok("이메일로 인증 코드를 발송했습니다. 인증 코드를 입력해주세요.");
    }

    // POST /api/users/verify-email
    // 이메일 인증 코드 확인 후 회원가입 완료
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailVerificationRequestDTO requestDTO){
        userService.registerComplete(requestDTO);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

}
