package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }
}