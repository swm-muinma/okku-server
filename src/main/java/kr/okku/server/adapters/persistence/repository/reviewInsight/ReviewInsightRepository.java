package kr.okku.server.adapters.persistence.repository.reviewInsight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewInsightRepository extends MongoRepository<ReviewInsightEntity, String> {
    Optional<ReviewInsightEntity> findByProductPkAndPlatform(String productPk, String platform);


    // 필요한 커스텀 메소드 추가
}
