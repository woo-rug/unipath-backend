package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture_time")
@Getter
@NoArgsConstructor
public class LectureTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String day; // "월", "화", "사이버" 등

    private String startTime; // "HH:MM:SS" 형식

    private String endTime;
}