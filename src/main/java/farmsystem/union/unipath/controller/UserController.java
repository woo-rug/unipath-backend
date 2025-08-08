package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.domain.CustomUserDetails;
import farmsystem.union.unipath.dto.*;
import farmsystem.union.unipath.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO){ // 반환 타입 수정
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUserId(), requestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginResponseDTO responseDTO = new LoginResponseDTO(userDetails.getUser());

        return ResponseEntity.ok(responseDTO); // DTO를 응답으로 보냄
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long id) {
        UserInfoDTO userInfo = userService.getUserInfoById(id);
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequestDTO requestDTO) {
        try {
            userService.changeUserPassword(id, requestDTO.getCurrentPassword(), requestDTO.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/{id}/career")
    public ResponseEntity<UserCareerInfoDTO> getUserCareer(@PathVariable Long id) {
        UserCareerInfoDTO careerInfo = userService.getUserCareerInfo(id);
        return ResponseEntity.ok(careerInfo);
    }

    @PutMapping("/{id}/career")
    public ResponseEntity<String> updateUserCareer(@PathVariable Long id, @RequestBody CareerChoiceDTO choiceDTO) {
        userService.updateUserCareer(id, choiceDTO.getCareerGroupId(), choiceDTO.getCareerId());
        return ResponseEntity.ok("진로 정보가 저장되었습니다.");
    }
}
