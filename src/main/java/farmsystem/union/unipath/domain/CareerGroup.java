package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class CareerGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // 직업군 이름

    @Column(nullable = false)
    private String description; // 직업군 설명

    @OneToMany(mappedBy = "careerGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    @Builder
    public CareerGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
