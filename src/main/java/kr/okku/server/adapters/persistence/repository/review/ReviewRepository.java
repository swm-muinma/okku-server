package kr.okku.server.adapters.persistence.repository.review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {
    List<ReviewEntity> findByProductPkOrderByRatingAsc(String productPk);

    // 필요한 커스텀 메소드 추가
}
