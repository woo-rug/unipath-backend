package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_course_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "class_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCourseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "semester_id", nullable = false)
    private int semesterId;

    @Builder
    public UserCourseHistory(User user, String classId, String className, int semesterId) {
        this.user = user;
        this.classId = classId;
        this.className = className;
        this.semesterId = semesterId;
    }
}