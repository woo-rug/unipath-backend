package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.Career;
import farmsystem.union.unipath.domain.CareerGroup;
import farmsystem.union.unipath.dto.CareerRecommendationDTO;
import farmsystem.union.unipath.dto.QuestionDTO;
import farmsystem.union.unipath.repository.CareerGroupRepository;
import farmsystem.union.unipath.repository.CareerRepository;
import farmsystem.union.unipath.repository.QuestionRepository;
import farmsystem.union.unipath.repository.QuestionWeightRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerGroupRepository careerGroupRepository;
    private final CareerRepository careerRepository;
    private final QuestionWeightRepository questionWeightRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public CareerRecommendationDTO recommendCareers(Map<Long, Integer> answers){

        // 1. 모든 직업군의 초기 점수를 0으로 초기화
        Iterable<CareerGroup> careerGroups = careerGroupRepository.findAll();
        Map<Long, Integer> scoreMap = new HashMap<>();
        for(CareerGroup group : careerGroups){
            scoreMap.put(group.getId(), 0);
        }

        // 2. 답변에 따라 직업군별 점수 계산
        answers.forEach((questionId, score) -> {
            questionWeightRepository.findByQuestionId(questionId)
                    .forEach(weight ->{
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

}
