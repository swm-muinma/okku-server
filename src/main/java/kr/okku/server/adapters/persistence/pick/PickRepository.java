package kr.okku.server.adapters.persistence.pick;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickRepository extends MongoRepository<PickEntity, String> {
    List<PickEntity> findByUserId(String userId);

    // 필요한 커스텀 메소드 추가
}
