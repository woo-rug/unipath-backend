package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.Career;
import farmsystem.union.unipath.domain.CareerGroup;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.dto.CareerDetailDTO;
import farmsystem.union.unipath.dto.CareerInfoDTO;
import farmsystem.union.unipath.dto.CareerRecommendationDTO;
import farmsystem.union.unipath.dto.QuestionDTO;
import farmsystem.union.unipath.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerGroupRepository careerGroupRepository;
    private final CareerRepository careerRepository;
    private final QuestionWeightRepository questionWeightRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public CareerRecommendationDTO recommendCareers(Map<Long, Integer> answers) {

        // 1. 모든 직업군의 초기 점수를 0으로 초기화
        Iterable<CareerGroup> careerGroups = careerGroupRepository.findAll();
        Map<Long, Integer> scoreMap = new HashMap<>();
        for (CareerGroup group : careerGroups) {
            scoreMap.put(group.getId(), 0);
        }

        // 2. 답변에 따라 직업군별 점수 계산
        answers.forEach((questionId, score) -> {
            questionWeightRepository.findByQuestionId(questionId)
                    .forEach(weight -> {
                        Long careerGroupId = weight.getCareerGroup().getId();
                        int currentScore = scoreMap.getOrDefault(careerGroupId, 0);
                        scoreMap.put(careerGroupId, currentScore + (score * weight.getWeight()));
                    });
        });

        // 3. 가장 높은 점수를 받은 직업군 찾기
        Long highestScoreCareerGroupId = scoreMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("추천 직업군을 찾을 수 없습니다."));

        // 4. 해당 직업군에 속한 직업 3개 조회
        CareerGroup recommendedGroup = careerGroupRepository.findById(highestScoreCareerGroupId)
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 직업군입니다."));
        List<Career> recommendedCareers = careerRepository.findByCareerGroup_Id(recommendedGroup.getId());

        // 5. 결과를 DTO로 변환 후 리턴
        return CareerRecommendationDTO.from(recommendedGroup, recommendedCareers);

    }

    @Transactional
    public List<QuestionDTO> getQuestions() {
        return questionRepository.findAllByOrderByIdAsc().stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CareerInfoDTO> getCareerInfo() {
        return StreamSupport.stream(careerGroupRepository.findAll().spliterator(), false)
                .map(this::mapToCareerInfoDTO)
                .collect(Collectors.toList());
    }

    private CareerInfoDTO mapToCareerInfoDTO(CareerGroup careerGroup) {
        List<CareerDetailDTO> careerDetailDTOs = careerRepository.findByCareerGroup_Id(careerGroup.getId()).stream()
                .map(career -> new CareerDetailDTO(career.getName(), career.getDescription()))
                .collect(Collectors.toList());
        return new CareerInfoDTO(careerGroup.getName(), careerDetailDTOs);
    }

    @Transactional
    public void setUserCareer(Long userId, String careerName, String careerGroupName) {
        Career career = careerRepository.findByName(careerName)
                .orElseThrow(() -> new IllegalArgumentException("Career not found with name: " + careerName));

        CareerGroup careerGroup = careerGroupRepository.findByName(careerGroupName)
                .orElseThrow(() -> new IllegalArgumentException("CareerGroup not found with name: " + careerGroupName));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.updateCareer(career, careerGroup);
        userRepository.save(user);
    }
    
}
