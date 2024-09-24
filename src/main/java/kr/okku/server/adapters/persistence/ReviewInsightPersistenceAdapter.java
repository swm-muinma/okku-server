package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightEntity;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightRepository;
import kr.okku.server.domain.ReviewInsightDomain;
import kr.okku.server.mapper.ReviewInsightMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewInsightPersistenceAdapter {

    private final ReviewInsightRepository reviewInsightRepository;

    // PickRepository 의존성 주입
    public ReviewInsightPersistenceAdapter(ReviewInsightRepository reviewInsightRepository) {
        this.reviewInsightRepository = reviewInsightRepository;
    }

    public Optional<ReviewInsightDomain> findByProductPkAndPlatform(String productPk, String platform) {
        // Repository에서 데이터를 조회하여 리스트로 받음
        List<ReviewInsightEntity> reviewInsightEntities = reviewInsightRepository.findAllByProductPkAndPlatform(productPk, platform);

        // 리스트가 비어있지 않다면 첫 번째 요소를 맵핑하여 Optional로 감싸고, 비어있다면 Optional.empty() 반환
        return reviewInsightEntities.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(ReviewInsightMapper.toDomain(reviewInsightEntities.get(0)));
    }

}
