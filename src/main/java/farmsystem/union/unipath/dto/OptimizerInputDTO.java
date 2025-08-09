package farmsystem.union.unipath.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Java(Spring Boot)에서 Python 최적화 스크립트로 전달하는 모든 데이터를 담는 컨테이너 DTO 입니다.
 * 이 객체 하나가 전체 JSON으로 변환되어 Python으로 넘어갑니다.
 */
@Data
@Builder
public class OptimizerInputDTO {

    /**
     * 이번 학기에 개설된 모든 강의 분반의 정보입니다.
     * (정형화된 시간 정보 포함)
     */
    private List<LectureDTO> lectures;

    /**
     * 위 분반들과 관련된 모든 원본 과목의 정보입니다.
     * (학점, 이수 구분 등)
     */
    private List<CourseDTO> courses;

    /**
     * 사용자의 모든 요구사항 정보입니다.
     * (기수강 내역 포함)
     */
    private UserInputDTO userInput;

}