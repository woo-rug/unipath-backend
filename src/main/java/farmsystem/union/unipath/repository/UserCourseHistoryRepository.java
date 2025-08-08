package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCourseHistoryRepository extends JpaRepository<UserCourseHistory, Long> {
    List<UserCourseHistory> findByUserAndSemesterId(User user, int semesterId);
    void deleteByUserAndSemesterId(User user, int semesterId);
    List<UserCourseHistory> findByUser(User user); // 이 메서드 추가
}