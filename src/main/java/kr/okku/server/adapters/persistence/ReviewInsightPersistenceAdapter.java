package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewInsightPersistenceAdapter {

    private final ReviewInsightRepository reviewInsightRepository;

    // PickRepository 의존성 주입
    public ReviewInsightPersistenceAdapter(ReviewInsightRepository reviewInsightRepository) {
        this.reviewInsightRepository = reviewInsightRepository;
    }
}
