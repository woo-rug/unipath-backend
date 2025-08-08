package farmsystem.union.unipath.dto;

import farmsystem.union.unipath.domain.User;
import lombok.Getter;

@Getter
public class UserCareerInfoDTO {
    private final String careerGroupName;
    private final String careerName;

    public UserCareerInfoDTO(User user) {
        this.careerGroupName = user.getSelectedCareerGroup() != null ? user.getSelectedCareerGroup().getName() : null;
        this.careerName = user.getSelectedCareer() != null ? user.getSelectedCareer().getName() : null;
    }
}
