package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.CareerGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CareerGroupRepository extends CrudRepository<CareerGroup, Long> {
    Optional<CareerGroup> findByName(String name);
}
