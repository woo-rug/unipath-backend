package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.*;
import farmsystem.union.unipath.service.CareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/career")
public class CareerController {

    private final CareerService careerService;

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getTestQuestions() {
        List<QuestionDTO> questions = careerService.getQuestions();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/test")
    public ResponseEntity<CareerRecommendationDTO> recommendCareers(@Valid @RequestBody CareerTestRequestDTO requestDTO) {
        CareerRecommendationDTO recommendation = careerService.recommendCareers(requestDTO.getAnswers());
        return ResponseEntity.ok(recommendation);
    }

    /**
     * 모든 직업 목록을 조회합니다.
     * @return (직업군, 직업, 직업 설명)을 포함하는 직업 정보 리스트
     */
    @GetMapping
    public ResponseEntity<List<CareerInfoDTO>> getAllCareers() {
        List<CareerInfoDTO> careerInfo = careerService.getCareerInfo();
        return ResponseEntity.ok(careerInfo);
    }

    /**
     * 사용자가 선택한 직업을 저장합니다.
     * @param requestDTO 선택한 직업 정보를 담은 DTO
     * @return 성공 시 200 OK
     */
    @PostMapping("/select/{userId}")
    public ResponseEntity<Void> selectCareer(@Valid @RequestBody CareerChoiceDTO requestDTO, @PathVariable Long userId) {
        careerService.setUserCareer(userId, requestDTO.getCareerName(), requestDTO.getCareerGroupName());
        return ResponseEntity.ok().build();
    }
}