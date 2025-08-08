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
        private final int total;

        // @Builder가 생성자를 만들지만, 접근 권한 문제를 해결하기 위해 public 생성자를 명시적으로 추가
        @Builder
        public CreditStatus(int current, int total) {
            this.current = current;
            this.total = total;
        }
    }
}