import { Document, Schema, model } from "mongoose";
import { FormEnum } from "@src/enum/form.enum";
import { UserDomain } from "@src/domain/user.domain";
import { createModel, mainConnection } from "@src/infra/mongo.config";

// UserEntity 클래스
class UserEntity {
  _id?: string;

  constructor(
    public name: string,
    public image: string,
    public height: number,
    public weight: number,
    public form: FormEnum,
    public is_premium: boolean,
    public kakao_id: string,
    public apple_id: string,
    public created_at: Date | null = new Date(),
    public updated_at: Date | null = new Date()
  ) {}
}

// Mongoose 스키마
const UserSchema: Schema<UserEntity & Document> = new Schema({
  name: { type: String, required: true },
  image: { type: String, required: false },
  height: { type: Number, required: false },
  weight: { type: Number, required: false },
  kakao_id: { type: String, unique: false },
  apple_id: { type: String, unique: false },
  is_premium: { type: Boolean, required: true, unique: false },
  form: { type: String, enum: Object.values(FormEnum), required: false },
  created_at: { type: Date, default: Date.now },
  updated_at: { type: Date, default: Date.now },
});

const UserModel = createModel<UserEntity & Document>(
  "User",
  UserSchema,
  mainConnection
);

class UserPersistenceMapper {
  static toDomain(entity: UserEntity): UserDomain {
    let res: UserDomain = new UserDomain(
      entity.name,
      entity.image,
      entity.height,
      entity.weight,
      entity.form
    );
    res.id = entity._id!;
    res.createdAt = entity.created_at;
    res.updatedAt = entity.updated_at;
    res.appleId = entity.apple_id;
    res.kakaoId = entity.kakao_id;
    res.isPremium = entity.is_premium;
    return res;
  }

  static toEntity(domain: UserDomain): UserEntity {
    let res: UserEntity = new UserEntity(
      domain.name,
      domain.image,
      domain.height,
      domain.weight,
      domain.form,
      domain.isPremium,
      domain.kakaoId,
      domain.appleId,
      domain.createdAt,
      domain.updatedAt
    );
    if (domain.id) {
      res._id = domain.id;
    }
    return res;
  }

  static partialDomainToPartialEntity(
    update: Partial<UserDomain>
  ): Partial<UserEntity> {
    const partialEntity: Partial<UserEntity> = {};

    if (update.name !== undefined) {
      partialEntity.name = update.name;
    }
    if (update.image !== undefined) {
      partialEntity.image = update.image;
    }
    if (update.height !== undefined) {
      partialEntity.height = update.height;
    }
    if (update.weight !== undefined) {
      partialEntity.weight = update.weight;
    }
    if (update.form !== undefined) {
      partialEntity.form = update.form;
    }
    partialEntity.updated_at = new Date();
    return partialEntity;
  }
}

export { UserPersistenceMapper, UserModel, UserEntity };
