package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.LectureTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LectureTimeDTO {
    private String day;
    private String startTime;
    private String endTime;

    public static LectureTimeDTO fromEntity(LectureTime lectureTime) {
        return LectureTimeDTO.builder()
                .day(lectureTime.getDay())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}