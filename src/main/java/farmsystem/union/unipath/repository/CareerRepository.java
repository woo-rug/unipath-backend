package farmsystem.union.unipath.repository;

import farmsystem.union.unipath.domain.Career;
import farmsystem.union.unipath.domain.CareerGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRepository extends CrudRepository<Career, Long> {
    List<Career> findByCareerGroup_Id(Long careerGroupId);
    Optional<Career> findByName(String name);
}
