import { CartRepository } from "@src/adapters/persistence/repository/cart.repository";
import { PickRepository } from "@src/adapters/persistence/repository/pick.repository";
import { CartDomain } from "@src/domain/cart.domain";
import { ErrorDomain } from "@src/domain/error.domain";
import { PageInfo } from "@src/dto/pageInfo.dto";
import mongoose from "mongoose";

const cartRepository = new CartRepository();
const pickRepository = new PickRepository();
const isValidObjectId = (id: string): boolean => mongoose.isValidObjectId(id);

export class CartService {
  async getMyCarts(
    userId: string,
    page: number,
    size: number
  ): Promise<{
    carts: {
      id: string;
      name: string;
      picksNum: number;
      picksImages: string[];
    }[];
    page: PageInfo;
  }> {
    console.log(userId);
    if (page < 1) {
      throw new ErrorDomain("page must be larger than 0", 400);
    }

    if (size < 1) {
      throw new ErrorDomain("size must be larger than 0", 400);
    }

    const cartDomains = await cartRepository.findByUserId(userId, page, size);

    if (!cartDomains) {
      throw new ErrorDomain("Cart not found by userId", 404);
    }
    const resCart = await Promise.all(
      cartDomains.carts.map(async (cart) => {
        // 각 pick_id에 대해 pick 정보를 가져옵니다.
        const pickIds = cart.pickItemIds;
        const picks = (
          await pickRepository.findByPickIds(pickIds, 1, cart.pickNum)
        ).picks;

        // pick 이미지들을 모아 하나의 배열로 만듭니다.
        const pickImages = picks.flatMap((pick) => pick.image).slice(0, 3); // 최대 3개의 이미지만 포함

        // 새로운 카트 객체를 만듭니다.
        return {
          id: cart.id!,
          name: cart.name,
          picksNum: pickIds.length,
          picksImages: pickImages,
        };
      })
    );

    return { carts: resCart, page: cartDomains.page };
  }

  async deleteCart(userId: string, cartId: string): Promise<string> {
    const cartInfo = await cartRepository.findById(cartId);
    if (cartInfo.userId != userId) {
      throw new ErrorDomain("not cart owner", 400);
    }
    const deletedCartId = await cartRepository.delete(cartId);
    if (!deletedCartId) {
      throw new ErrorDomain("Cart not found", 404);
    }
    return deletedCartId;
  }

  async createCart(
    userId: string,
    name: string,
    pickIds: string[]
  ): Promise<CartDomain> {
    if (!name) {
      throw new ErrorDomain("'name' is required", 400);
    }
    const cart = await cartRepository.create(userId, name, pickIds);
    if (cart) {
      return cart;
    }
    throw new ErrorDomain("Cart not created", 404);
  }

  async movePicks(
    userId: string,
    pickIds: string[],
    sourceCartId: string,
    destinationCartId: string,
    isDeleteFromOrigin: boolean
  ): Promise<{
    source: { cartId: string; pickIds: string[] };
    destination: { cartId: string; pickIds: string[] };
  }> {
    if (isDeleteFromOrigin == null) {
      throw new ErrorDomain("'isDeleteFromOrigin' filed is required", 400);
    }
    if (!destinationCartId) {
      throw new ErrorDomain("'destinationCartId' filed is required", 400);
    }
    if (isDeleteFromOrigin == false) {
      const movedPickIds: string[] | null = await cartRepository.addToCart(
        pickIds,
        destinationCartId
      );
      if (!movedPickIds) {
        throw new ErrorDomain("already exist in cart", 400);
      }
      if (pickIds.length == 0) {
        throw new ErrorDomain("pickIds is required", 400);
      }
      const picksInfo = await pickRepository.findByPickIds(
        pickIds,
        1,
        pickIds.length
      );
      picksInfo.picks.forEach((el) => {
        if (el.userId != userId) {
          throw new ErrorDomain("not pick owner", 400);
        }
      });
      return {
        source: {
          cartId: sourceCartId ? sourceCartId : "__all__",
          pickIds: movedPickIds,
        },
        destination: { cartId: destinationCartId, pickIds: movedPickIds },
      };
    }
    if (isDeleteFromOrigin == true) {
      const movedPickIds: string[] | null = await cartRepository.moveCart(
        pickIds,
        sourceCartId,
        destinationCartId
      );
      if (!movedPickIds) {
        throw new ErrorDomain("can not moved picks", 500);
      }
      return {
        source: {
          cartId: sourceCartId ? sourceCartId : "__all__",
          pickIds: [],
        },
        destination: { cartId: destinationCartId, pickIds: movedPickIds },
      };
    }
    throw new ErrorDomain("can not moved picks with server error", 500);
  }
}
