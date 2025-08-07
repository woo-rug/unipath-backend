package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Career {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 직업 이름

    @Column(nullable = false)
    private String description; // 직업 상세 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="career_group_id")
    private CareerGroup careerGroup; // 직업이 속한 직업군

    @Builder
    public Career(String name, String description, CareerGroup careerGroup) {
        this.name = name;
        this.description = description;
        this.careerGroup = careerGroup;
    }

}
