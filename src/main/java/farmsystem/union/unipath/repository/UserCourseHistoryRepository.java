package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCourseHistoryRepository extends JpaRepository<UserCourseHistory, Long> {

    List findByUserAndSemesterId(User user, int semesterId);
    List findByUser(User user);

    @Modifying
    @Query("DELETE FROM UserCourseHistory u WHERE u.user = :user AND u.semesterId = :semesterId")
    void deleteByUserAndSemesterId(@Param("user") User user, @Param("semesterId") int semesterId);
}