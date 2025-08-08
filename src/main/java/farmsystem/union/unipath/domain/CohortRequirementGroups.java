package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cohort_requirement_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"admission_year", "group_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CohortRequirementGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admission_year", nullable = false)
    private String admissionYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private RequirementGroups requirementGroup;

    @Builder
    public CohortRequirementGroups(String admissionYear, RequirementGroups requirementGroup) {
        this.admissionYear = admissionYear;
        this.requirementGroup = requirementGroup;
    }
}