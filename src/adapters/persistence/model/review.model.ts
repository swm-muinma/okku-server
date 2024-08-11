import { ReviewDomain } from "@src/domain/review.domain";
import { Document, Schema, model } from "mongoose";

// ReviewEntity 클래스
class ReviewEntity {
  _id?: string;

  constructor(
    public rating: string,
    public gender: string,
    public option: string,
    public criterion: any,
    public height: number,
    public weight: number,
    public top_size: string,
    public bottom_size: string,
    public content: string,
    public image_url: string,
    public product_pk: string,
    public platform: string
  ) {}
}

// ReviewSchema 정의
const ReviewSchema: Schema<ReviewEntity & Document> = new Schema({
  rating: { type: String, required: false },
  gender: { type: String, required: false },
  option: { type: String, required: false },
  criterion: { type: Schema.Types.Mixed, required: false },
  height: { type: Number, required: false },
  weight: { type: Number, required: false },
  top_size: { type: String, required: false },
  bottom_size: { type: String, required: false },
  content: { type: String, required: true },
  image_url: { type: String, required: false },
  product_pk: { type: String, required: true },
  platform: { type: String, required: true },
});

// ReviewModel 정의
const ReviewModel = model<ReviewEntity & Document>("Review", ReviewSchema);

// ReviewPersistenceMapper 정의
class ReviewPersistenceMapper {
  static toDomain(entity: ReviewEntity): ReviewDomain {
    let res: ReviewDomain = new ReviewDomain(
      entity._id!,
      entity.rating,
      entity.gender,
      entity.option,
      entity.criterion,
      entity.height,
      entity.weight,
      entity.top_size,
      entity.bottom_size,
      entity.content,
      entity.image_url,
      entity.product_pk,
      entity.platform
    );
    return res;
  }

  static toEntity(domain: ReviewDomain): ReviewEntity {
    let res: ReviewEntity = new ReviewEntity(
      domain.rating,
      domain.gender,
      domain.option,
      domain.criterion,
      domain.height,
      domain.weight,
      domain.topSize,
      domain.bottomSize,
      domain.content,
      domain.imageUrl,
      domain.productPk,
      domain.platform
    );
    if (domain.id) {
      res._id = domain.id;
    }
    return res;
  }

  static partialDomainToPartialEntity(
    update: Partial<ReviewDomain>
  ): Partial<ReviewEntity> {
    const partialEntity: Partial<ReviewEntity> = {};

    if (update.rating !== undefined) {
      partialEntity.rating = update.rating;
    }
    if (update.gender !== undefined) {
      partialEntity.gender = update.gender;
    }
    if (update.option !== undefined) {
      partialEntity.option = update.option;
    }
    if (update.criterion !== undefined) {
      partialEntity.criterion = update.criterion;
    }
    if (update.height !== undefined) {
      partialEntity.height = update.height;
    }
    if (update.weight !== undefined) {
      partialEntity.weight = update.weight;
    }
    if (update.topSize !== undefined) {
      partialEntity.top_size = update.topSize;
    }
    if (update.bottomSize !== undefined) {
      partialEntity.bottom_size = update.bottomSize;
    }
    if (update.content !== undefined) {
      partialEntity.content = update.content;
    }
    if (update.imageUrl !== undefined) {
      partialEntity.image_url = update.imageUrl;
    }
    if (update.productPk !== undefined) {
      partialEntity.product_pk = update.productPk;
    }
    if (update.platform !== undefined) {
      partialEntity.platform = update.platform;
    }
    return partialEntity;
  }
}

export { ReviewPersistenceMapper, ReviewModel, ReviewEntity };
