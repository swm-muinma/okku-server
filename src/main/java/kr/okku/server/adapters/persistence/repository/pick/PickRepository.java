package kr.okku.server.adapters.persistence.repository.pick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickRepository extends MongoRepository<PickEntity, String> {
    Page<PickEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<PickEntity> findByUserIdOrderByCreatedAtDesc(String userId);

    List<PickEntity> findByIdInOrderByCreatedAtDesc(List<String> pickIds);

    Page<PickEntity> findByIdInOrderByCreatedAtDesc(List<String> pickIds,Pageable pageable);

    Optional<PickEntity> findByIdOrderByCreatedAtDesc(String id);

    long deleteByIdIn(List<String> pickIds);
}
