package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.Lecture;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
public class LectureDTO {

    // 분반의 고유 ID (lectures 테이블의 PK)
    private Long id;

    // 원본 과목의 학수번호 (e.g., "CS101")
    private String courseId;

    // 해당 분반의 모든 강의 시간 정보 리스트
    private List<LectureTimeDTO> times;

    /**
     * Lecture 엔티티를 LectureDto로 변환하는 정적 팩토리 메서드입니다.
     * @param lecture 변환할 Lecture 엔티티 객체
     * @return 변환된 LectureDTO 객체
     */
    public static LectureDTO fromEntity(Lecture lecture) {
        // Lecture 엔티티에 포함된 모든 LectureTime 객체들을 LectureTimeDto로 변환합니다.
        List<LectureTimeDTO> timeDtos = lecture.getLectureTimes().stream()
                .map(LectureTimeDTO::fromEntity)
                .collect(Collectors.toList());

        return LectureDTO.builder()
                .id(lecture.getId())
                .courseId(lecture.getCourse().getClassId())
                .times(timeDtos)
                .build();
    }
}