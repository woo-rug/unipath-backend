package farmsystem.union.unipath.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GraduationStatusDTO {
    private final CreditStatus totalCredits;
    private final CreditStatus commonGeneralEducationCredits;
    private final CreditStatus basicEducationCredits;
    private final CreditStatus bsmCredits;
    private final CreditStatus majorCredits;

    @Getter
    public static class CreditStatus {
        private final int current;
        private final int required;

        @Builder
        public CreditStatus(int current, int required) {
            this.current = current;
            this.required = required;
        }
    }
}