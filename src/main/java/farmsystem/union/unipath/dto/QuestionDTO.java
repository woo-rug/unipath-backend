package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Question;
import lombok.Getter;

@Getter
public class QuestionDTO {
    private final Long id;
    private final String questionText;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.questionText = question.getQuestion();
    }
}
