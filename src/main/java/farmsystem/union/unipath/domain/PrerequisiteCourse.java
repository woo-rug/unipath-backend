package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prerequisite_courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"class_id", "prerequisite_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrerequisiteCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private Course prerequisite;

    @Builder
    public PrerequisiteCourse(Course course, Course prerequisite) {
        this.course = course;
        this.prerequisite = prerequisite;
    }
}