package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.mapper.PickMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewPersistenceAdapter {

    private final ReviewRepository reviewRepository;

    // PickRepository 의존성 주입
    public ReviewPersistenceAdapter(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
}
