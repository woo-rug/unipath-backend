package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {

    // 회원 고유 id값
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 이름
    @Column(nullable = false)
    private String name;

    // 회원 학번
    @Column(nullable = false, unique = true)
    private String userId;

    // 회원 비밀번호
    @Column(nullable = false)
    private String password;


    @Column(nullable = false, unique = true)
    private String email;

    @Builder
    public User(String name, String userId, String password, String email) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
    }
}
