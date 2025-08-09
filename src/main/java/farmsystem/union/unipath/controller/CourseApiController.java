package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.dto.CourseResponse;
import farmsystem.union.unipath.dto.CreditInfoDTO;
import farmsystem.union.unipath.service.CourseService;
import farmsystem.union.unipath.service.GraduationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseApiController {

    private final CourseService courseService;
    private final GraduationService graduationService;

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return courses.stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/graduation-status/{userId}")
    public List<CreditInfoDTO> getGraduationStatus(@PathVariable Long userId) {
        return graduationService.getGraduationStatusAsList(userId);
    }
}