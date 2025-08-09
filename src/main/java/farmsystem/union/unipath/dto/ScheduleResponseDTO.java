package farmsystem.union.unipath.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import farmsystem.union.unipath.domain.Lecture;
import farmsystem.union.unipath.domain.LectureTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDTO {

    private String status;
    private String message;
    private List<RecommendedLectureDTO> schedule;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedLectureDTO {
        private Long lectureId; // 고유 아이디
        private String lectureCode; // 강의 분반 코드
        private String name; // 강의명
        private String professor; // 교수명
        private String credit; // 학점
        private String type; // 이수구분
        private String classroom; // 강의실
        private List<LectureTimeDto> lectureTimes; // 강의 배정 시간

        public static RecommendedLectureDTO from(Lecture lecture) {
            return RecommendedLectureDTO.builder()
                    .lectureId(lecture.getId())
                    .lectureCode(lecture.getLectureCode())
                    .name(lecture.getCourse().getClassName())
                    .professor(lecture.getProfessor())
                    .credit(lecture.getCourse().getCredits())
                    .type(lecture.getCourse().getRequiredDivision())
                    .classroom(lecture.getClassroom())
                    .lectureTimes(lecture.getLectureTimes().stream()
                            .map(LectureTimeDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureTimeDto {
        private String dayOfWeek;
        private String startTime;
        private String endTime;

        public static LectureTimeDto from(LectureTime lectureTime) {
            return LectureTimeDto.builder()
                    .dayOfWeek(lectureTime.getDay())
                    .startTime(lectureTime.getStartTime())
                    .endTime(lectureTime.getEndTime())
                    .build();
        }
    }
}
