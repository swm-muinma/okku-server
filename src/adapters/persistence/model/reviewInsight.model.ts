import { createModel, reviewInsightConnection } from "@src/infra/mongo.config";
import { Document, Schema, Model, model } from "mongoose";

// ReviewSummaryEntity 클래스
class ReviewSummaryEntity {
  description: string;
  reviewIds: string[];

  constructor(description: string, reviewIds: string[]) {
    this.description = description;
    this.reviewIds = reviewIds;
  }
}

// ReviewInsightEntity 클래스
class ReviewInsightEntity {
  _id?: string;
  platform: string;
  product_pk: string;
  cautions: ReviewSummaryEntity[];
  positives: ReviewSummaryEntity[];

  constructor(
    platform: string,
    product_pk: string,
    cautions: ReviewSummaryEntity[],
    positives: ReviewSummaryEntity[]
  ) {
    this.platform = platform;
    this.product_pk = product_pk;
    this.cautions = cautions;
    this.positives = positives;
  }
}

// ReviewSummarySchema 정의
const ReviewSummarySchema: Schema<ReviewSummaryEntity & Document> = new Schema({
  description: { type: String, required: true },
  reviewIds: [{ type: String, required: true }],
});

// ReviewInsightSchema 정의
const ReviewInsightSchema: Schema<ReviewInsightEntity & Document> = new Schema({
  platform: { type: String, required: true },
  product_pk: { type: String, required: true },
  cautions: { type: [ReviewSummarySchema], required: true },
  positives: { type: [ReviewSummarySchema], required: true },
});

// createReviewInsightModel 함수 정의: 컬렉션 이름을 platform 필드의 값으로 설정
export const createReviewInsightModel = (
  platform: string
): Model<ReviewInsightEntity & Document> => {
  return createModel<ReviewInsightEntity & Document>(
    platform,
    ReviewInsightSchema,
    reviewInsightConnection
  );
};

export { ReviewInsightEntity, ReviewSummaryEntity };
