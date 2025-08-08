package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Course;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseResponse {
    private String classId;
    private String className;

    public CourseResponse(Course course) {
        this.classId = course.getClassId();
        this.className = course.getClassName();
    }
}