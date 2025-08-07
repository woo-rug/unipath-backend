package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
public class QuestionWeight {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_group_id", nullable = false)
    private CareerGroup careerGroup;

    @Column(nullable = false)
    private int weight;

    @Builder
    public QuestionWeight(Question question, CareerGroup careerGroup, int weight) {
        this.question = question;
        this.careerGroup = careerGroup;
        this.weight = weight;
    }
}
