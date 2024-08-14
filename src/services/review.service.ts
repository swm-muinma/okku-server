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
  PickDTO,
  ProductReviewDTO,
  ReviewSectionDTO,
} from "@src/dto/summarizedReviews.dto";

const scraperAdapter = new ScraperAdapter();
const reviewInsightRepository = new ReviewInsightRepository();
const reviewRepository = new ReviewRepository();
const pickRepository = new PickRepository();

const okkuIds: string[] = [];

export class ReviewService {
  async getItemInfoWithoutLogin(url: string, okkuId: string): Promise<PickDTO> {
    try {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      const scrapedData = await scraperAdapter.scrape(url);
      return {
        image: scrapedData.thumbnail_url,
        name: scrapedData.name,
        price: scrapedData.price,
        url: url,
      };
    } catch (err) {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      console.log(err);
      throw new ErrorDomain("error with scrape", 500);
    }
  }

  async getReviewsWithoutLogin(
    product_pk: string,
    platform: string,
    okkuId: string
  ): Promise<ProductReviewDTO> {
    try {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      const insight =
        await reviewInsightRepository.getInsightsByProductPkWithPolling(
          product_pk,
          platform
        );
      console.log("generate Insight");
      console.log(insight);

      const reviews: ReviewDomain[] =
        await reviewRepository.getReviewsByProductPk(product_pk, platform, 100);

      okkuIds.push(okkuId);
      // "", 0으로 들어가는 것들은 추후 제거되어야함. 원래 pick정보도 여기서 리턴했었어서 남아있는 잔재
      return this.toDto(platform, reviews, insight, null, "", "", 0, "url");
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
      if (pick == null) {
        return {} as ProductReviewDTO;
      }
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
        pick.platform.name,
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
    platform: string,
    reviews: ReviewDomain[],
    insight: ReviewInsightDomain,
    pickDomain: PickDomain | null,
    image: string,
    name: string,
    price: number,
    url: string
  ): Promise<ProductReviewDTO> {
    // ReviewSectionDTO를 만드는 헬퍼 함수
    async function createReviewSectionDTO(
      description: string,
      reviewIds: string[]
    ): Promise<ReviewSectionDTO> {
      // reviewIds를 기반으로 CommentDTO 리스트를 생성
      const commentsPromises = reviewIds.map(async (reviewId) => {
        const review = reviews.find((r) => r.id.toString() === reviewId);
        if (!review) {
          const tempReview = await reviewRepository.findById(
            reviewId,
            platform
          );
          if (!tempReview) {
            return {
              name: "", // 기본값을 빈 문자열로 설정
              height: 0, // 기본값을 0으로 설정
              weight: 0, // 기본값을 0으로 설정
              comment: "",
              image: "", // 기본값을 빈 문자열로 설정
            };
          }
          return {
            name: tempReview.gender || "", // 기본값을 빈 문자열로 설정
            height: tempReview.height || 0, // 기본값을 0으로 설정
            weight: tempReview.weight || 0, // 기본값을 0으로 설정
            comment: tempReview.content,
            image: tempReview.imageUrl || "", // 기본값을 빈 문자열로 설정
          };
        }
        return {
          name: review.gender || "", // 기본값을 빈 문자열로 설정
          height: review.height || 0, // 기본값을 0으로 설정
          weight: review.weight || 0, // 기본값을 0으로 설정
          comment: review.content,
          image: review.imageUrl || "", // 기본값을 빈 문자열로 설정
        };
      });

      const comments = await Promise.all(commentsPromises);

      return {
        content: description,
        count: comments.length,
        comments: comments,
      };
    }

    // insight 객체에서 cons와 pros를 생성
    const consPromises = insight.cautions.map((caution) =>
      createReviewSectionDTO(caution.description, caution.reviewIds)
    );
    const prosPromises = insight.positives.map((positive) =>
      createReviewSectionDTO(positive.description, positive.reviewIds)
    );

    const [cons, pros] = await Promise.all([
      Promise.all(consPromises),
      Promise.all(prosPromises),
    ]);

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
