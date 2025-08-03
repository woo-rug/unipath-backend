package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);
}
