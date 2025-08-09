package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    // N+1 문제를 해결하기 위해 fetch join을 사용합니다.
    @Query("SELECT DISTINCT l FROM Lecture l JOIN FETCH l.lectureTimes")
    List<Lecture> findAllWithLectureTimes();

    // 추천된 강의 ID 목록으로 상세 정보와 시간 정보를 함께 조회합니다.
    @Query("SELECT DISTINCT l FROM Lecture l JOIN FETCH l.lectureTimes WHERE l.id IN :ids")
    List<Lecture> findAllByIdWithLectureTimes(@Param("ids") List<Long> ids);
}
