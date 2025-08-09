package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Course;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Python 최적화 스크립트로 전달하기 위한 Course(원본 과목) 정보 DTO 입니다.
 */
@Data
@Builder
public class CourseDTO {

    // 과목의 학수번호 (e.g., "CS101")
    private String id;

    // 과목명
    private String name;

    // 학점
    private int credit;

    // Python 스크립트가 이해하기 쉽도록 단순화된 이수 구분 ("전공" 또는 "교양")
    private String ctype;

    /**
     * 직업군별 추천 점수입니다.
     * (e.g., {"백엔드 개발자": 10, "프론트엔드 개발자": 5})
     */
    private Map<String, Integer> jobRecommendations;

    /**
     * Course 엔티티를 CourseDto로 변환하는 정적 팩토리 메서드입니다.
     * @param course 변환할 Course 엔티티 객체
     * @return 변환된 CourseDto 객체
     */
    public static CourseDTO fromEntity(Course course) {
        // DB에 저장된 상세한 이수 구분을 "전공", "교양"으로 매핑합니다.
        String mappedType = mapCourseType(course.getRequiredDivision());

        // 직업군별 추천 점수 Map 생성
        Map<String, Integer> jobRecs = new HashMap<>();
        jobRecs.put("web_dev", course.getScoreWebDev());
        jobRecs.put("app_dev", course.getScoreAppDev());
        jobRecs.put("game_dev", course.getScoreGameDev());
        jobRecs.put("data_ai_dev", course.getScoreDataAiDev());
        jobRecs.put("infosec", course.getScoreInfosec());
        jobRecs.put("cloud_devops", course.getScoreCloudDevops());
        jobRecs.put("tech_pm", course.getScoreTechPm());
        jobRecs.put("special_tech", course.getScoreSpecialTech());

        return CourseDTO.builder()
                .id(course.getClassId())
                .name(course.getClassName())
                .credit(Integer.parseInt(course.getCredits()))
                .ctype(mappedType)
                .jobRecommendations(jobRecs)
                .build();
    }

    /**
     * 데이터베이스의 상세한 이수 구분 문자열을
     * "전공", "교양"이라는 두 가지 카테고리로 단순화하는 헬퍼 메서드입니다.
     * @param dbCourseType 데이터베이스에서 가져온 원본 이수 구분 (e.g., "전공전문")
     * @return "전공", "교양", "기타" 중 하나
     */
    private static String mapCourseType(String dbCourseType) {
        if (dbCourseType == null) {
            return "기타";
        }

        switch (dbCourseType) {
            case "전공기초":
            case "전공전문":
            case "전공필수": // 혹시 모를 케이스 추가
                return "전공";
            case "일반교양":
            case "공통교양":
            case "학문기초":
            case "교양필수": // 혹시 모를 케이스 추가
                return "교양";
            default:
                return "기타";
        }
    }
}
