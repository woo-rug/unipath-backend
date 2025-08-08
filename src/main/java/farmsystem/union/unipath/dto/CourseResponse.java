package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Course;
import lombok.Getter;

@Getter
public class CourseResponse {
    private final String classId;
    private final String className;
    private final String credits; // 학점 필드 추가
    private final boolean isMajor; // 전공 여부 필드 추가

    public CourseResponse(Course course) {
        this.classId = course.getClassId();
        this.className = course.getClassName();
        this.credits = course.getCredits(); // 학점 정보 매핑

        // 이수 구분에 따라 전공 여부 판단
        String division = course.getRequiredDivision();
        this.isMajor = "전공기초".equals(division) || "전공전문".equals(division);
    }
}