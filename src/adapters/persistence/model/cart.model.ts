import { CartDomain } from "@src/domain/cart.domain";
import { createModel, mainConnection } from "@src/infra/mongo.config";
import { Document, Schema } from "mongoose";

class CartEntity {
  _id?: string;

  constructor(
    public user_id: string,
    public name: string,
    public pick_num: number,
    public pick_item_ids: string[],
    public created_at: Date | null,
    public updated_at: Date | null
  ) {
    this.created_at = created_at ?? new Date();
    this.updated_at = updated_at ?? new Date();
  }
}

const CartSchema: Schema<CartEntity & Document> = new Schema({
  user_id: { type: String, required: true },
  name: { type: String, required: true },
  pick_num: { type: Number, required: false },
  pick_item_ids: { type: [String], required: false },
  created_at: { type: Date, default: Date.now },
  updated_at: { type: Date, default: Date.now },
});

const CartModel = createModel<CartEntity & Document>(
  "Cart",
  CartSchema,
  mainConnection
);

class CartPersistenceMapper {
  static toDomain(entity: CartEntity): CartDomain {
    let res: CartDomain = new CartDomain(
      entity.user_id,
      entity.name,
      entity.pick_num,
      entity.pick_item_ids
    );
    res.id = entity._id!;
    res.createdAt = entity.created_at;
    res.updatedAt = entity.updated_at;
    return res;
  }

  static toEntity(domain: CartDomain): CartEntity {
    let res: CartEntity = new CartEntity(
      domain.userId,
      domain.name,
      domain.pickNum,
      domain.pickItemIds,
      domain.createdAt,
      domain.updatedAt
    );
    if (domain.id) {
      res._id = domain.id;
    }
    return res;
  }

  static partialDomainToPartialEntity(
    update: Partial<CartDomain>
  ): Partial<CartEntity> {
    const partialEntity: Partial<CartEntity> = {};

    if (update.userId !== undefined) {
      partialEntity.user_id = update.userId;
    }
    if (update.name !== undefined) {
      partialEntity.name = update.name;
    }
    if (update.pickNum !== undefined) {
      partialEntity.pick_num = update.pickNum;
    }
    if (update.pickItemIds !== undefined) {
      partialEntity.pick_item_ids = update.pickItemIds;
    }
    partialEntity.updated_at = new Date();
    return partialEntity;
  }
}

export { CartPersistenceMapper, CartModel, CartEntity };
