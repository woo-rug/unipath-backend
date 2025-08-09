package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.Career;
import farmsystem.union.unipath.domain.CareerGroup;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.dto.EmailVerificationRequestDTO;
import farmsystem.union.unipath.dto.UserCareerInfoDTO;
import farmsystem.union.unipath.dto.UserInfoDTO;
import farmsystem.union.unipath.dto.UserRegistrationRequestDTO;
import farmsystem.union.unipath.repository.CareerGroupRepository;
import farmsystem.union.unipath.repository.CareerRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final CareerRepository careerRepository;
    private final CareerGroupRepository careerGroupRepository;

    // 이메일 코드 임시 저장 공간
    private final Map<String, UserRegistrationRequestDTO> tempUserStorage = new HashMap<>();
    private final Map<String, String> emailCodeStorage = new HashMap<>();

    // 1단계 : 회원가입 정보를 임시로 저장하고 이메일 인증 코드 발송
    @Transactional
    public void tempRegister(UserRegistrationRequestDTO requestDTO) {
        // 1. 기존 유저 확인
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if(userRepository.existsByUserId(requestDTO.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 학번입니다.");
        }

        // 2. 이메일 코드 생성
        String emailCode = generateEmailCode();

        // 3. 이메일 전송
        String subject = "유니패스 회원가입 인증 코드";
        String text = "회원가입을 위한 인증 코드는 **" + emailCode + "** 입니다.";
        emailSenderService.sendEmail(requestDTO.getEmail(), subject, text);

        //4. 회원가입 정보와 인증 코드 임시 저장소 보관
        tempUserStorage.put(requestDTO.getEmail(), requestDTO);
        emailCodeStorage.put(requestDTO.getEmail(), emailCode);
    }

    // 2단계 : 이메일 인증 코드를 확인하고 회원가입 완료
    @Transactional
    public void registerComplete(EmailVerificationRequestDTO requestDTO) {

        // 1. 임시 저장소에서 정보 가져오기
        UserRegistrationRequestDTO tempUser = tempUserStorage.get(requestDTO.getEmail());
        String emailCodeAnswer = emailCodeStorage.get(requestDTO.getEmail());

        // 2. 유효성 검사
        if (tempUser == null || emailCodeAnswer == null) {
            throw new IllegalArgumentException("유효하지 않은 회원입니다.");
        }
        else if (!requestDTO.getEmailCode().equals(emailCodeAnswer)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");
        }

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(tempUser.getPassword());

        // 4. User 객체 생성 및 데이터베이스에 저장
        User user = User.builder()
                .name(tempUser.getName())
                .userId(tempUser.getUserId())
                .password(encryptedPassword)
                .email(tempUser.getEmail())
                .admissionYear(tempUser.getUserId().substring(0, 4))
                .build();
        userRepository.save(user);

        // 5. 회원가입 최종 완료 후 임시 저장소 정보 삭제
        tempUserStorage.remove(requestDTO.getEmail());
        emailCodeStorage.remove(requestDTO.getEmail());
    }

    // 이메일 인증 코드 생성 함수
    private String generateEmailCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Transactional(readOnly = true)
    public UserInfoDTO getUserInfoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserInfoDTO(user);
    }

    @Transactional
    public void changeUserPassword(Long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 인코딩 및 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserCareerInfoDTO getUserCareerInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserCareerInfoDTO(user);
    }

    @Transactional
    public void updateUserCareer(Long userId, String careerGroupName, String careerName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CareerGroup careerGroup = careerGroupRepository.findByName(careerGroupName)
                .orElseThrow(() -> new IllegalArgumentException("다음 이름의 직업군을 찾을 수 없습니다: " + careerGroupName));

        Career career = careerRepository.findByName(careerName)
                .orElseThrow(() -> new IllegalArgumentException("다음 이름의 직업을 찾을 수 없습니다: " + careerName));

        user.setSelectedCareerGroup(careerGroup);
        user.setSelectedCareer(career);
        userRepository.save(user);
    }

}
