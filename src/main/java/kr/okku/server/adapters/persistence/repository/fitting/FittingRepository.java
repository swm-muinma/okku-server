package kr.okku.server.adapters.persistence.repository.fitting;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FittingRepository extends MongoRepository<FittingEntity, String> {

    List<FittingEntity> findByIdInOrderByCreatedAtDesc(List<String> ids);
}
