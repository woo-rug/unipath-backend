package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCourseHistoryRepository extends JpaRepository<UserCourseHistory, Long> {
    // 특정 사용자와 학기 ID로 수강 기록을 조회하는 메서드
    List<UserCourseHistory> findByUserAndSemesterId(User user, int semesterId);

    // 특정 사용자와 학기 ID에 해당하는 모든 수강 기록을 삭제하는 메서드
    void deleteByUserAndSemesterId(User user, int semesterId);
}