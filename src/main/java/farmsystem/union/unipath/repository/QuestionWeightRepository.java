package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.QuestionWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionWeightRepository extends JpaRepository<QuestionWeight, Long> {
    List<QuestionWeight> findByQuestionId(Long questionId);
    List<QuestionWeight> findByCareerGroupId(Long careerGroupId);
}
