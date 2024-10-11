package kr.okku.server.adapters.persistence.repository.fitting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FittingRepository extends MongoRepository<FittingEntity, String> {
}
