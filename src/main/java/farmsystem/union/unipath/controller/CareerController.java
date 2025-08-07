package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.CareerRecommendationDTO;
import farmsystem.union.unipath.dto.CareerTestRequestDTO;
import farmsystem.union.unipath.service.CareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/career")
public class CareerController {

    private final CareerService careerService;

    @PostMapping("/test")
    public ResponseEntity<CareerRecommendationDTO> recommendCareers(@Valid @RequestBody CareerTestRequestDTO requestDTO) {
        CareerRecommendationDTO recommendation = careerService.recommendCareers(requestDTO.getAnswers());
        return ResponseEntity.ok(recommendation);
    }
}
