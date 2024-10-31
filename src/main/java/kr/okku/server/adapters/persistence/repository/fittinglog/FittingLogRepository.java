package kr.okku.server.adapters.persistence.repository.fittinglog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FittingLogRepository extends MongoRepository<FittingLogEntity, String> {
    default List<FittingLogEntity> findAllByCreatedAtDesc() {
        return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
