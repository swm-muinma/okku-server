package kr.okku.server.adapters.persistence.repository.review;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {

    // 특정 플랫폼과 제품 키로 리뷰를 조회하는 메서드
    Optional<ReviewEntity> findByPlatformAndProductKey(String platform, String productKey);

    Optional<ReviewEntity> findByIsDoneScrapeReviews(boolean isDoneScrapeReviews);
}
