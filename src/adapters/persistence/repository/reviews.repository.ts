import { ReviewDomain } from "@src/domain/review.domain";
import {
  ReviewEntity,
  createReviewModel,
  ReviewPersistenceMapper,
} from "../model/review.model";
import { ErrorDomain } from "@src/domain/error.domain";
import mongoose, { Types } from "mongoose";

class ReviewRepository {
  /**
   * Get up to `limitNum` reviews by product primary key, sorted by rating in ascending order.
   * @param productPk - The primary key of the product.
   * @param platform - The platform to use for selecting the correct collection.
   * @param limitNum - The maximum number of reviews to retrieve.
   * @returns A promise that resolves to an array of ReviewDomain objects.
   */
  public async getReviewsByProductPk(
    productPk: string,
    platform: string,
    limitNum: number
  ): Promise<ReviewDomain[]> {
    if (!productPk) {
      throw new ErrorDomain("Product PK is required", 400);
    }

    try {
      // 플랫폼에 따라 ReviewModel 생성
      const ReviewModel = createReviewModel(platform);

      // Find reviews by productPk, sorted by rating in ascending order, limit to limitNum
      const reviews = await ReviewModel.find({ product_pk: productPk })
        .sort({ rating: 1 }) // Sort by rating ascending
        .limit(limitNum) // Limit the number of results
        .exec();

      if (!reviews.length) {
        return []; // No reviews found for this productPk
      }

      // Map to domain models
      const reviewDomains = reviews.map(ReviewPersistenceMapper.toDomain);
      return reviewDomains;
    } catch (error) {
      console.error("Error getting reviews by product PK:", error);
      throw new ErrorDomain("Error getting reviews by product PK", 500);
    }
  }

  public async findById(id: string, platform: string): Promise<ReviewDomain> {
    if (!Types.ObjectId.isValid(id)) {
      throw new ErrorDomain("Invalid ID format", 400);
    }

    try {
      // 플랫폼에 따라 ReviewModel 생성
      const ReviewModel = createReviewModel(platform);

      const review = await ReviewModel.findById(id).exec();
      if (!review) {
        throw new ErrorDomain("Cannot find review with given ID", 404);
      }
      return ReviewPersistenceMapper.toDomain(review);
    } catch (err) {
      throw new ErrorDomain("Error finding review by ID", 500);
    }
  }

  public async create(
    review: ReviewDomain,
    platform: string
  ): Promise<ReviewDomain> {
    try {
      const ReviewModel = createReviewModel(platform);
      const reviewEntity = ReviewPersistenceMapper.toEntity(review);
      const newReview = new ReviewModel(reviewEntity);
      const savedReview = await newReview.save();
      return ReviewPersistenceMapper.toDomain(savedReview);
    } catch (error) {
      console.error("Error creating review:", error);
      throw new ErrorDomain("Error creating review", 500);
    }
  }

  public async delete(reviewIds: string[], platform: string): Promise<boolean> {
    try {
      const ReviewModel = createReviewModel(platform);
      const result = await ReviewModel.deleteMany({
        _id: { $in: reviewIds },
      }).exec();
      return result.deletedCount === reviewIds.length;
    } catch (error) {
      console.error("Error deleting reviews:", error);
      throw new ErrorDomain("Error deleting reviews", 500);
    }
  }

  isValidObjectId = (id: string): boolean => mongoose.isValidObjectId(id);

  // 기타 필요한 메서드들을 이곳에 추가할 수 있습니다.
}

export { ReviewRepository };
