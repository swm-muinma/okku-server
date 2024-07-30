import { CartRepository } from "src/adapters/persistence/repository/cart.repository";
import { CartDomain } from "src/domain/cart.domain";
import { ErrorDomain } from "src/domain/error.domain";
import { PageInfo } from "src/dto/pageInfo.dto";
const userId = "test_user_id";

const cartRepository = new CartRepository();
export class CartService {
  async getMyCarts(
    userId: string,
    page: number,
    size: number
  ): Promise<{ carts: CartDomain[]; page: PageInfo }> {
    if (page < 1) {
      throw new ErrorDomain("page must be larger than 0", 400);
    }

    if (size < 1) {
      throw new ErrorDomain("size must be larger than 0", 400);
    }

    const res = await cartRepository.findByUserId(userId, page, size);
    if (!res) {
      throw new ErrorDomain("Cart not found by userId", 404);
    }
    return res;
  }

  async deleteCart(cartId: string): Promise<string> {
    const deletedCartId = await cartRepository.delete(cartId);
    if (!deletedCartId) {
      throw new ErrorDomain("Cart not found", 404);
    }
    return deletedCartId;
  }

  async createCart(name: string, pickIds: string[]): Promise<CartDomain> {
    if (!name) {
      throw new ErrorDomain("'name' is required", 400);
    }
    const cart = await cartRepository.create(userId, name, pickIds);
    if (cart) {
      return cart;
    }
    throw new ErrorDomain("Cart not created", 404);
  }

  async movePicks() {}
}
