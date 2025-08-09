package farmsystem.union.unipath.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareerInfoDTO {
    private String careerGroupName;
    private List<CareerDetailDTO> careers;
}
