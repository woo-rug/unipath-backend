package farmsystem.union.unipath.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;

@Getter
public class CareerTestRequestDTO {

    @NotNull(message="답변은 필수입니다.")
    private Map<Long, Integer> answers; // Key : 질문 id, Value : 질문 답변 점수 (1~5점)
}
