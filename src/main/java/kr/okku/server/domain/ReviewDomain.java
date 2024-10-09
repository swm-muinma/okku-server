package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewDomain {
    private String id;
    private String platform;
    private String productKey;
    private boolean isDoneScrapeReviews;
    private List<ReviewDetailDomain> reviews;

    public double calculateAverageRating() {
        if (this.reviews == null || this.reviews.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        int count = 0;

        for (ReviewDetailDomain review : this.reviews) {
            if (review.getRating() != null) {
                sum += review.getRating();
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) sum / count;
    }
}
