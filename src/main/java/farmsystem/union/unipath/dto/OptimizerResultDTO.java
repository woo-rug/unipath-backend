package farmsystem.union.unipath.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Python 최적화 스크립트로부터 반환받은 결과를 담기 위한 DTO 입니다.
 */
@Data
@NoArgsConstructor // JSON 역직렬화를 위해 기본 생성자가 반드시 필요합니다.
public class OptimizerResultDTO {

    /**
     * 최적화 결과의 상태를 나타냅니다.
     * "SUCCESS": 성공
     * "FAILED": 해를 찾지 못함
     * "ERROR": 스크립트 실행 중 오류 발생
     */
    private String status;

    /**
     * 최적화에 성공했을 경우, 선택된 강의 분반(Lecture)의 ID 목록입니다.
     */
    private List<Long> lectureIds;

    /**
     * 스크립트 실행 중 에러가 발생했을 경우, 에러 메시지를 담습니다.
     */
    private String message;

}