package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user") // 테이블 이름을 users로 명시 (user는 예약어일 수 있음)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String admissionYear;

    // --- 신규 필드 추가 ---
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_career_group_id")
    private CareerGroup selectedCareerGroup;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_career_id")
    private Career selectedCareer;

    @Builder
    public User(String name, String userId, String password, String email, String admissionYear) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.admissionYear = admissionYear;
    }

    public void updateCareer(Career career, CareerGroup careerGroup) {
        this.selectedCareer = career;
        this.selectedCareerGroup = careerGroup;
    }
}
