import {
  ReviewInsightDomain,
  ReviewSummaryDomain,
} from "@src/domain/reviewInsight.domain";
import {
  ReviewInsightEntity,
  ReviewSummaryEntity,
  createReviewInsightModel,
} from "../model/reviewInsight.model";
import { ErrorDomain } from "@src/domain/error.domain";
import mongoose, { Types } from "mongoose";

class ReviewInsightRepository {
  /**
   * Polling to get insights by product primary key.
   * @param productPk - The primary key of the product.
   * @param platform - The platform to use for selecting the correct collection.
   * @returns A promise that resolves to a ReviewInsightDomain object if found, or rejects with an error after 3 minutes.
   */
  public async getInsightsByProductPkWithPolling(
    productPk: string,
    platform: string
  ): Promise<ReviewInsightDomain> {
    if (!productPk) {
      throw new ErrorDomain("Product PK is required", 400);
    }

    const maxPollingTime = 180000; // 3 minutes in milliseconds
    const pollingInterval = 1000; // 1 second in milliseconds
    let elapsedTime = 0;

    const ReviewInsightModel = createReviewInsightModel(platform);

    return new Promise<ReviewInsightDomain>((resolve, reject) => {
      const poll = async () => {
        try {
          const insights = await ReviewInsightModel.findOne({
            product_pk: productPk,
          }).exec();

          if (insights) {
            resolve(this.toDomain(insights));
          } else if (elapsedTime >= maxPollingTime) {
            reject(
              new ErrorDomain(
                "No insights found within the given timeframe",
                408
              )
            ); // 408: Request Timeout
          } else {
            elapsedTime += pollingInterval;
            setTimeout(poll, pollingInterval);
          }
        } catch (error) {
          console.error("Error during polling for insights:", error);
          reject(new ErrorDomain("Error during polling for insights", 500));
        }
      };

      poll();
    });
  }

  /**
   * Get insights by product primary key.
   * @param productPk - The primary key of the product.
   * @param platform - The platform to use for selecting the correct collection.
   * @returns A promise that resolves to a ReviewInsightDomain object.
   */
  public async getByProductPk(
    productPk: string,
    platform: string
  ): Promise<ReviewInsightDomain> {
    if (!productPk) {
      throw new ErrorDomain("Product PK is required", 400);
    }

    try {
      const ReviewInsightModel = createReviewInsightModel(platform);

      const insights: ReviewInsightEntity | null =
        await ReviewInsightModel.findOne({
          product_pk: productPk,
        }).exec();

      if (!insights) {
        throw new ErrorDomain(
          "No insights found for the given product PK",
          404
        );
      }

      return this.toDomain(insights);
    } catch (error) {
      console.error("Error getting insights by product PK:", error);
      throw new ErrorDomain("Error getting insights by product PK", 500);
    }
  }

  // public async create(
  //   insight: ReviewInsightDomain,
  //   platform: string
  // ): Promise<ReviewInsightDomain> {
  //   try {
  //     const ReviewInsightModel = createReviewInsightModel(platform);
  //     const insightEntity = this.toEntity(insight);
  //     const newInsight = new ReviewInsightModel(insightEntity);
  //     const savedInsight = await newInsight.save();
  //     return this.toDomain(savedInsight);
  //   } catch (error) {
  //     console.error("Error creating insight:", error);
  //     throw new ErrorDomain("Error creating insight", 500);
  //   }
  // }

  // public async deleteByProductPk(
  //   productPk: string,
  //   platform: string
  // ): Promise<boolean> {
  //   try {
  //     const ReviewInsightModel = createReviewInsightModel(platform);
  //     const result = await ReviewInsightModel.deleteOne({
  //       product_pk: productPk,
  //     }).exec();
  //     return result.deletedCount === 1;
  //   } catch (error) {
  //     console.error("Error deleting insight:", error);
  //     throw new ErrorDomain("Error deleting insight", 500);
  //   }
  // }

  // public async updateByProductPk(
  //   productPk: string,
  //   platform: string,
  //   updateData: Partial<ReviewInsightDomain>
  // ): Promise<ReviewInsightDomain> {
  //   try {
  //     const ReviewInsightModel = createReviewInsightModel(platform);
  //     const updatedInsight = await ReviewInsightModel.findOneAndUpdate(
  //       { product_pk: productPk },
  //       updateData,
  //       { new: true }
  //     ).exec();

  //     if (!updatedInsight) {
  //       throw new ErrorDomain(
  //         "Cannot find insight to update with the given product PK",
  //         404
  //       );
  //     }

  //     return this.toDomain(updatedInsight);
  //   } catch (error) {
  //     console.error("Error updating insight:", error);
  //     throw new ErrorDomain("Error updating insight", 500);
  //   }
  // }

  // Helper method to map entity to domain
  private toDomain(entity: ReviewInsightEntity): ReviewInsightDomain {
    return new ReviewInsightDomain(
      entity._id!,
      entity.platform,
      entity.product_pk,
      entity.cautions.map(
        (caution) =>
          new ReviewSummaryDomain(caution.description, caution.reviewIds)
      ),
      entity.positives.map(
        (positive) =>
          new ReviewSummaryDomain(positive.description, positive.reviewIds)
      )
    );
  }

  // Helper method to map domain to entity
  private toEntity(domain: ReviewInsightDomain): ReviewInsightEntity {
    return new ReviewInsightEntity(
      domain.platform,
      domain.product_pk,
      domain.cautions.map(
        (caution) =>
          new ReviewSummaryEntity(caution.description, caution.reviewIds)
      ),
      domain.positives.map(
        (positive) =>
          new ReviewSummaryEntity(positive.description, positive.reviewIds)
      )
    );
  }

  isValidObjectId = (id: string): boolean => mongoose.isValidObjectId(id);
}

export { ReviewInsightRepository };
