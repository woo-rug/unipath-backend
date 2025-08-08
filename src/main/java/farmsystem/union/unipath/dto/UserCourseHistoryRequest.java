package farmsystem.union.unipath.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserCourseHistoryRequest {
    private Long userId; // 사용자 ID (User 엔티티의 id)
    private int semesterId; // 1~9 정수 값 (1학년 1학기 -> 1, 4학년 2학기 -> 8, 기타 -> 9)
    private List<CourseInfo> courses; // 수강 과목 리스트

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CourseInfo {
        private String classId;
        private String className;
    }
}