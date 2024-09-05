package kr.okku.server.adapters.persistence.repository.reviewInsight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewInsightRepository extends MongoRepository<ReviewInsightEntity, String> {
    List<ReviewInsightEntity> findAllByProductPkAndPlatform(String productPk, String platform);


    // 필요한 커스텀 메소드 추가
}
