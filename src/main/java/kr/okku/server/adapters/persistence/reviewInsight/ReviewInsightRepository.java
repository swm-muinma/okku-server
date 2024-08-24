package kr.okku.server.adapters.persistence.reviewInsight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewInsightRepository extends MongoRepository<ReviewInsightEntity, String> {
    List<ReviewInsightEntity> findByUserId(String userId);

    // 필요한 커스텀 메소드 추가
}
