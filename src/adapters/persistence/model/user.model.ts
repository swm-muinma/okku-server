import { Document, Schema, model } from "mongoose";
import { FormEnum } from "src/enum/form.enum";
import { UserDomain } from "src/domain/user.domain";

// UserEntity 클래스
class UserEntity {
  _id?: string;

  constructor(
    public name: string,
    public image: string,
    public height: number,
    public weight: number,
    public form: FormEnum,
    public created_at: Date | null = new Date(),
    public updated_at: Date | null = new Date()
  ) {}
}

// Mongoose 스키마
const UserSchema: Schema<UserEntity & Document> = new Schema({
  name: { type: String, required: true },
  image: { type: String, required: true },
  height: { type: Number, required: true },
  weight: { type: Number, required: true },
  form: { type: String, enum: Object.values(FormEnum), required: true },
  created_at: { type: Date, default: Date.now },
  updated_at: { type: Date, default: Date.now },
});

const UserModel = model<UserEntity & Document>("User", UserSchema);

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
    return res;
  }

  static toEntity(domain: UserDomain): UserEntity {
    let res: UserEntity = new UserEntity(
      domain.name,
      domain.image,
      domain.height,
      domain.weight,
      domain.form,
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
