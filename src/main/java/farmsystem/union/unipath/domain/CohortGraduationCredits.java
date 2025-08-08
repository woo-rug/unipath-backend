package farmsystem.union.unipath.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cohort_graduation_credits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CohortGraduationCredits {

    @Id
    @Column(name = "admission_year", nullable = false)
    private String admissionYear;

    @Column(name = "total_credits_required", nullable = false)
    private int totalCreditsRequired;

    @Column(name = "common_general_education_credits", nullable = false)
    private int commonGeneralEducationCredits;

    @Column(name = "basic_education_credits", nullable = false)
    private int basicEducationCredits;

    @Column(name = "bsm_credits", nullable = false)
    private int bsmCredits;

    @Column(name = "major_credits", nullable = false)
    private int majorCredits;

    @Builder
    public CohortGraduationCredits(String admissionYear, int totalCreditsRequired, int commonGeneralEducationCredits, int basicEducationCredits, int bsmCredits, int majorCredits) {
        this.admissionYear = admissionYear;
        this.totalCreditsRequired = totalCreditsRequired;
        this.commonGeneralEducationCredits = commonGeneralEducationCredits;
        this.basicEducationCredits = basicEducationCredits;
        this.bsmCredits = bsmCredits;
        this.majorCredits = majorCredits;
    }
}