import { Document, Schema, model } from "mongoose";
import { PickDomain, PlatformDomain } from "src/domain/pick.domain";

class PickEntity {
  _id?: string;

  constructor(
    public user_id: string,
    public name: string,
    public price: number,
    public image: string,
    public platform: PlatformDomain, // Here PlatformDomain is included
    public created_at: Date | null = new Date(),
    public updated_at: Date | null = new Date()
  ) {}
}

const PickSchema: Schema<PickEntity & Document> = new Schema({
  user_id: { type: String, required: true },
  name: { type: String, required: true },
  price: { type: Number, required: true },
  image: { type: String, required: true },
  platform: {
    name: { type: String, required: true },
    image: { type: String, required: true },
    url: { type: String, required: true },
  },
  created_at: { type: Date, default: Date.now },
  updated_at: { type: Date, default: Date.now },
});

const PickModel = model<PickEntity & Document>("Pick", PickSchema);

class PickPersistenceMapper {
  static toDomain(entity: PickEntity): PickDomain {
    const platform = new PlatformDomain(
      entity.platform.name,
      entity.platform.image,
      entity.platform.url
    );
    let res: PickDomain = new PickDomain(
      entity.user_id,
      entity.name,
      entity.price,
      entity.image,
      platform
    );
    res.id = entity._id!;
    res.createdAt = entity.created_at;
    res.updatedAt = entity.updated_at;
    return res;
  }

  static toEntity(domain: PickDomain): PickEntity {
    const platform = {
      name: domain.platform.name,
      image: domain.platform.image,
      url: domain.platform.url,
    };

    let res: PickEntity = new PickEntity(
      domain.userId,
      domain.name,
      domain.price,
      domain.image,
      platform,
      domain.createdAt,
      domain.updatedAt
    );
    if (domain.id) {
      res._id = domain.id;
    }
    return res;
  }

  static partialDomainToPartialEntity(
    update: Partial<PickDomain>
  ): Partial<PickEntity> {
    const partialEntity: Partial<PickEntity> = {};

    if (update.userId !== undefined) {
      partialEntity.user_id = update.userId;
    }
    if (update.name !== undefined) {
      partialEntity.name = update.name;
    }
    if (update.price !== undefined) {
      partialEntity.price = update.price;
    }
    if (update.image !== undefined) {
      partialEntity.image = update.image;
    }
    if (update.platform !== undefined) {
      partialEntity.platform = {
        name: update.platform.name,
        image: update.platform.image,
        url: update.platform.url,
      };
    }
    partialEntity.updated_at = new Date();
    return partialEntity;
  }
}

export { PickPersistenceMapper, PickModel, PickEntity };
