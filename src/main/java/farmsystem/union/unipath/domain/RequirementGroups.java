package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "requirement_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequirementGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    @Column(name = "selection_type", nullable = false)
    private String selectionType;

    @Builder
    public RequirementGroups(String groupName, String selectionType) {
        this.groupName = groupName;
        this.selectionType = selectionType;
    }
}