package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
