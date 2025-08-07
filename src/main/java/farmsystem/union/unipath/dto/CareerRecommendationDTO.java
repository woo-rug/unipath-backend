package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Career;
import farmsystem.union.unipath.domain.CareerGroup;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CareerRecommendationDTO {

    private String recommendedCareerGroupName;
    private String recommendedCareerGroupDescription;
    private List<CareerDetail> recommendedCareers;

    @Getter
    @Builder
    public static class CareerDetail {
        private String name;
        private String description;
    }

    public static CareerRecommendationDTO from(CareerGroup careerGroup, List<Career> careers) {

        // 추천할 직업군에 해당하는 3가지를 CareerDetail 클래스 객체로 리스트화
        List<CareerDetail> careerDetails = careers.stream()
                .map(career -> CareerDetail.builder()
                        .name(career.getName())
                        .description(career.getDescription())
                        .build())
                .toList();

        // 직업군, 직업군 설명, 직업 3개 리스트를 리턴
        return CareerRecommendationDTO.builder()
                .recommendedCareerGroupName(careerGroup.getName())
                .recommendedCareerGroupDescription(careerGroup.getDescription())
                .recommendedCareers(careerDetails)
                .build();
    }
}
