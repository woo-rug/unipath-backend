// woo-rug/unipath-backend/unipath-backend-45cc33c60f9e50d5ca6f41f738e5e83791eb77fc/src/main/java/farmsystem/union/unipath/dto/UserInputDTO.java

package farmsystem.union.unipath.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 시간표 생성을 위해 사용자로부터 직접 입력받는 요구사항 DTO 입니다.
 * 클라이언트에서 서버로 전송됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

    /**
     * 사용자가 "반드시 수강하고 싶은" 과목의 학수번호 목록입니다.
     * (e.g., ["CS301", "GE205"])
     */
    private List<String> includeCourses;

    /**
     * 사용자가 "절대 수강하고 싶지 않은" 과목의 학수번호 목록입니다.
     */
    private List<String> excludeCourses;

    /**
     * 이수 구분별 목표 학점입니다.
     * (e.g., {"전공": 9, "교양": 6})
     * ⭐️ [핵심 수정] 이 필드를 클라이언트가 직접 전송하도록 변경합니다.
     */
    private Map<String, Integer> targetCredits;

    /**
     * 사용자가 이미 수강 완료한 과목의 ID 목록입니다.
     * 이 필드는 서버의 OptimizerService에서 DB 조회 후 채워집니다.
     */
    private List<String> completedCourseIds;

    /**
     * 시간표 생성 시 고려할 사용자의 선호도 목록입니다.
     * (e.g., ["공강 최소화", "오전 수업 선호", "휴일 최대화", "연속강의 최소화"])
     */
    private List<String> preferences;

    /**
     * 사용자의 직업군 정보입니다.
     * 이 필드는 서버의 OptimizerService에서 DB 조회 후 채워집니다.
     */
    private String jobGroup;
}