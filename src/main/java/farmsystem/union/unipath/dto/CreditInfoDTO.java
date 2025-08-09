package farmsystem.union.unipath.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditInfoDTO {
    private String category;
    private Integer currentCredit;
    private Integer requiredCredit;
}
