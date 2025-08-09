package farmsystem.union.unipath.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 분반코드 (e.g., "CS101-01")
    @Column(nullable = false, unique = true)
    private String lectureCode;

    // 강의실 정보
    private String classroom;

    // 교수명
    private String professor;

    // 원본 과목 정보 (courses 테이블과 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // 이 분반에 속한 모든 강의 시간 정보 (lecture_time 테이블과 연결)
    // Lecture가 저장되거나 삭제될 때 관련된 LectureTime도 함께 처리됩니다 (cascade, orphanRemoval).
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("lecture_times")
    private List<LectureTime> lectureTimes = new ArrayList<>();

}
