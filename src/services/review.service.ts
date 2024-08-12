import { ScraperAdapter } from "@src/adapters/crawlling/scraper.adapter";
import { PickRepository } from "@src/adapters/persistence/repository/pick.repository";
import { ReviewInsightRepository } from "@src/adapters/persistence/repository/reviewInsight.repository";
import { ReviewRepository } from "@src/adapters/persistence/repository/reviews.repository";
import { ErrorDomain } from "@src/domain/error.domain";
import { PickDomain } from "@src/domain/pick.domain";
import { ReviewDomain } from "@src/domain/review.domain";
import { ReviewInsightDomain } from "@src/domain/reviewInsight.domain";
import {
  CommentDTO,
  ProductReviewDTO,
  ReviewSectionDTO,
} from "@src/dto/summarizedReviews.dto";

const scraperAdapter = new ScraperAdapter();
const reviewInsightRepository = new ReviewInsightRepository();
const reviewRepository = new ReviewRepository();
const pickRepository = new PickRepository();

const okkuIds: string[] = [];

export class ReviewService {
  async getReviewsWithoutLogin(
    url: string,
    okkuId: string
  ): Promise<ProductReviewDTO> {
    try {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }

      const scrapedData = await scraperAdapter.scrape(url);
      console.log("scrape: ", scrapedData);

      const insight =
        await reviewInsightRepository.getInsightsByProductPkWithPolling(
          scrapedData.product_pk,
          scrapedData.platform
        );

      const reviews: ReviewDomain[] =
        await reviewRepository.getReviewsByProductPk(
          scrapedData.product_pk,
          scrapedData.platform,
          100
        );

      okkuIds.push(okkuId);
      return this.toDto(
        reviews,
        insight,
        null,
        scrapedData.thumbnail_url,
        scrapedData.name,
        scrapedData.price,
        url
      );
    } catch (err) {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      console.log(err);
      throw new ErrorDomain("error with scrape", 500);
    }
  }

  async getReviews(pickId: string): Promise<ProductReviewDTO> {
    try {
      const pick = await pickRepository.findById(pickId);
      const insight =
        await reviewInsightRepository.getInsightsByProductPkWithPolling(
          pick.pk,
          pick.platform.name
        );

      const reviews: ReviewDomain[] =
        await reviewRepository.getReviewsByProductPk(
          pick.pk,
          pick.platform.name,
          100
        );

      return this.toDto(
        reviews,
        insight,
        null,
        pick.image,
        pick.name,
        pick.price,
        pick.url
      );
    } catch (err) {
      console.log(err);
      throw new ErrorDomain("error with scrape", 500);
    }
  }

  private async toDto(
    reviews: ReviewDomain[],
    insight: ReviewInsightDomain,
    pickDomain: PickDomain | null,
    image: string,
    name: string,
    price: number,
    url: string
  ): Promise<ProductReviewDTO> {
    const cons: ReviewSectionDTO[] = [];
    const pros: ReviewSectionDTO[] = [];

    // ReviewSectionDTO를 만드는 헬퍼 함수
    function createReviewSectionDTO(
      description: string,
      reviewIds: string[]
    ): ReviewSectionDTO {
      // reviewIds를 기반으로 ReviewDomain 객체를 찾아 CommentDTO 리스트를 생성
      const comments: CommentDTO[] = reviewIds.map((reviewId) => {
        const review = reviews.find((r) => r.id === reviewId);
        return review
          ? {
              name: review.gender || "", // 기본값을 빈 문자열로 설정
              height: review.height || 0, // 기본값을 0으로 설정
              weight: review.weight || 0, // 기본값을 0으로 설정
              comment: review.content,
              image: review.imageUrl || "", // 기본값을 빈 문자열로 설정
            }
          : {
              name: "", // 기본값을 빈 문자열로 설정
              height: 0, // 기본값을 0으로 설정
              weight: 0, // 기본값을 0으로 설정
              comment: "No content", // 기본값을 적절한 문자열로 설정
              image: "", // 기본값을 빈 문자열로 설정
            };
      });

      return {
        content: description,
        count: comments.length,
        comments: comments,
      };
    }

    // insight 객체에서 cons와 pros를 생성
    insight.cautions.forEach((caution) => {
      cons.push(createReviewSectionDTO(caution.description, caution.reviewIds));
    });

    insight.positives.forEach((positive) => {
      pros.push(
        createReviewSectionDTO(positive.description, positive.reviewIds)
      );
    });

    // ProductReviewDTO를 반환
    return {
      pick: {
        id: pickDomain ? pickDomain.id! : undefined,
        image: image,
        name: name,
        price: price,
        url: url,
      },
      reviews: {
        cons: cons,
        pros: pros,
      },
    };
  }
}
