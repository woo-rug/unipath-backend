package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.dto.CourseResponse;
import farmsystem.union.unipath.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseApiController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return courses.stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
    }
}