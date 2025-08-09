// woo-rug/unipath-backend/unipath-backend-45cc33c60f9e50d5ca6f41f738e5e83791eb77fc/src/main/java/farmsystem/union/unipath/dto/CourseDTO.java

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

    private String id;
    private String name;
    private int credit;
    // ⭐️ [핵심 수정] Python이 사용할 수 있도록 단순화된 이수 구분 ("전공", "교양", "기타")
    private String ctype;
    private Map<String, Integer> jobRecommendations;

    /**
     * Course 엔티티를 CourseDto로 변환하는 정적 팩토리 메서드입니다.
     */
    public static CourseDTO fromEntity(Course course) {
        // ⭐️ [핵심 수정] DB의 상세한 이수 구분을 "전공", "교양" 등의 한글 문자열로 다시 매핑합니다.
        String mappedType = mapCourseType(course.getRequiredDivision());

        Map<String, Integer> jobRecs = new HashMap<>();
        jobRecs.put("web_dev", course.getScoreWebDev());
        jobRecs.put("app_dev", course.getScoreAppDev());
        jobRecs.put("game_dev", course.getScoreGameDev());
        jobRecs.put("data_ai_dev", course.getScoreDataAiDev());
        jobRecs.put("infosec", course.getScoreInfosec());
        jobRecs.put("cloud_devops", course.getScoreCloudDevops());
        jobRecs.put("tech_pm", course.getScoreTechPm());
        jobRecs.put("special_tech", course.getScoreSpecialTech());

        int creditValue = 0;
        try {
            if (course.getCredits() != null && !course.getCredits().isEmpty()) {
                creditValue = Integer.parseInt(course.getCredits());
            }
        } catch (NumberFormatException e) {
            System.err.println("Could not parse credit: " + course.getCredits());
        }


        return CourseDTO.builder()
                .id(course.getClassId())
                .name(course.getClassName())
                .credit(creditValue)
                .ctype(mappedType) // "전공", "교양" 값을 그대로 사용
                .jobRecommendations(jobRecs)
                .build();
    }

    /**
     * ⭐️ [핵심 수정] 데이터베이스의 상세한 이수 구분 문자열을
     * "전공", "교양"이라는 두 가지 카테고리로 단순화하는 원래의 헬퍼 메서드로 복구합니다.
     */
    private static String mapCourseType(String dbCourseType) {
        if (dbCourseType == null) {
            return "기타";
        }

        switch (dbCourseType) {
            case "전공기초":
            case "전공전문":
            case "전공필수":
                return "전공";
            case "일반교양":
            case "공통교양":
            case "학문기초":
            case "교양필수":
                return "교양";
            default:
                return "기타";
        }
    }
}