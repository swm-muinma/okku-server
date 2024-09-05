package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightEntity;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightRepository;
import kr.okku.server.domain.ReviewInsightDomain;
import kr.okku.server.mapper.ReviewInsightMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewInsightPersistenceAdapter {

    private final ReviewInsightRepository reviewInsightRepository;

    // PickRepository 의존성 주입
    public ReviewInsightPersistenceAdapter(ReviewInsightRepository reviewInsightRepository) {
        this.reviewInsightRepository = reviewInsightRepository;
    }

    public ReviewInsightDomain findByProductPkAndPlatform(String productPk, String platform){
        List<ReviewInsightEntity> reviewInsightEntities = reviewInsightRepository.findAllByProductPkAndPlatform(productPk,platform);
        System.out.println(reviewInsightEntities);
        return ReviewInsightMapper.toDomain(reviewInsightEntities.get(0));
    }
}
