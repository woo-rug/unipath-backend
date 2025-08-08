package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByOrderByIdAsc();
}
