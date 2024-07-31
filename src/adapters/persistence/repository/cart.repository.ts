import { CartDomain } from "src/domain/cart.domain";
import {
  CartModel,
  CartPersistenceMapper,
} from "src/adapters/persistence/model/cart.model";
import { ErrorDomain } from "src/domain/error.domain";
import { PageInfo } from "src/dto/pageInfo.dto";
import { ClientSession } from "mongoose";

class CartRepository {
  /**
   * Find carts by user ID.
   * @param userId - The user ID to find carts for.
   * @returns A promise that resolves to an array of CartDomain or null.
   */
  public async findByUserId(
    userId: string,
    page: number,
    size: number
  ): Promise<{ carts: CartDomain[]; page: PageInfo }> {
    try {
      const skip = (page - 1) * size;

      // Get total count of carts
      const totalDataCnt = await CartModel.countDocuments({
        user_id: userId,
      }).exec();

      // Get paginated carts
      const carts = await CartModel.find({ user_id: userId })
        .skip(skip)
        .limit(size)
        .exec();

      // Map to domain models
      const cartDomains = carts.map(CartPersistenceMapper.toDomain);

      // Calculate pagination info
      const totalPages = Math.ceil(totalDataCnt / size);
      const isLastPage = page >= totalPages;
      const isFirstPage = page === 1;

      const pageInfo: PageInfo = {
        totalDataCnt,
        totalPages,
        isLastPage,
        isFirstPage,
        requestPage: page,
        requestSize: size,
      };

      return { carts: cartDomains, page: pageInfo };
    } catch (error) {
      console.error("Error finding carts by user ID:", error);
      throw new ErrorDomain("Error finding carts by user ID", 500);
    }
  }

  /**
   * Create a new cart for a user.
   * @param userId - The user ID to create a cart for.
   * @returns A promise that resolves to a boolean indicating success or null.
   */
  public async create(
    userId: string,
    name: string,
    pickIds: string[]
  ): Promise<CartDomain | null> {
    try {
      const newCart = new CartModel({
        user_id: userId,
        name: name,
        pick_num: 0,
        pick_item_ids: pickIds,
        pick_images: [],
        created_at: new Date(),
        updated_at: new Date(),
      });
      const savedCart = await newCart.save();
      return CartPersistenceMapper.toDomain(savedCart);
    } catch (error) {
      console.error("Error creating cart:", error);
      throw new ErrorDomain("Error creating cart:", 500);
    }
  }

  /**
   * Delete carts by user ID.
   * @param userId - The user ID to delete carts for.
   * @returns A promise that resolves to an array of CartDomain of deleted carts or null.
   */
  public async delete(cartId: string): Promise<string | null> {
    try {
      const entity = await CartModel.deleteOne({
        _id: cartId,
      }).exec();
      if (entity.deletedCount == 0) return null;
      return cartId;
    } catch (error) {
      console.error("Error deleting carts by cartId:", error);
      throw new ErrorDomain("Error deleting carts by cartId", 500);
    }
  }

  private async addToCartWithSession(
    pickIds: string[],
    cartId: string,
    session: ClientSession
  ): Promise<string[] | null> {
    try {
      const updatedTargetCart = await CartModel.updateOne(
        { _id: cartId },
        { $addToSet: { pick_item_ids: { $each: pickIds } } },
        { session }
      ).exec();
      return updatedTargetCart.modifiedCount > 0 ? pickIds : null;
    } catch (error) {
      console.error("Error adding picks to cart with session:", error);
      throw new ErrorDomain("Error adding picks to cart with session", 500);
    }
  }

  public async deletePickFromAllCart(
    pickIds: string[]
  ): Promise<string[] | null> {
    const session = await CartModel.startSession();
    session.startTransaction();

    try {
      const updatedCarts = await CartModel.updateMany(
        { pick_item_ids: { $in: pickIds } },
        { $pull: { pick_item_ids: { $in: pickIds } } },
        { session }
      ).exec();

      await session.commitTransaction();
      session.endSession();

      return updatedCarts.modifiedCount > 0 ? pickIds : null;
    } catch (error) {
      await session.abortTransaction();
      session.endSession();

      console.error("Error deleting picks from all carts:", error);
      throw new ErrorDomain("Error deleting picks from all carts", 500);
    }
  }

  private async deleteFromCartWithSession(
    pickIds: string[],
    cartId: string,
    session: ClientSession
  ): Promise<string[] | null> {
    try {
      // Update the cart by pulling the pick IDs from the specified cart
      const updatedCart = await CartModel.updateOne(
        { _id: cartId, pick_item_ids: { $in: pickIds } },
        { $pull: { pick_item_ids: { $in: pickIds } } },
        { session }
      ).exec();

      // Return the pick IDs if any were deleted, otherwise return null
      return updatedCart.modifiedCount > 0 ? pickIds : null;
    } catch (error) {
      console.error("Error deleting picks from cart with session:", error);
      throw new ErrorDomain("Error deleting picks from cart with session", 500);
    }
  }
  /**
   * Move picks to a different cart.
   * @param pickIds - The pick IDs to move.
   * @param sourceCartId - The source cart ID.
   * @param destinationCartId - The target cart ID.
   * @returns A promise that resolves to an array of moved pick IDs or null.
   */
  public async moveCart(
    pickIds: string[],
    sourceCartId: string,
    destinationCartId: string
  ): Promise<string[] | null> {
    const session = await CartModel.startSession();
    session.startTransaction();
    try {
      // Add picks to the target cart
      const addedPicks = await this.addToCartWithSession(
        pickIds,
        destinationCartId,
        session
      );

      // Remove picks from the original carts
      await this.deleteFromCartWithSession(pickIds, sourceCartId, session);

      await session.commitTransaction();
      return addedPicks;
    } catch (error) {
      await session.abortTransaction();
      console.error("Error moving picks to different cart:", error);
      throw new ErrorDomain("Error moving picks to different cart", 500);
    } finally {
      session.endSession();
    }
  }

  /**
   * Add picks to a cart.
   * @param pickIds - The pick IDs to add.
   * @param cartId - The target cart ID.
   * @returns A promise that resolves to an array of added pick IDs or null.
   */
  public async addToCart(
    pickIds: string[],
    cartId: string
  ): Promise<string[] | null> {
    const session = await CartModel.startSession();
    session.startTransaction();
    try {
      const result = await this.addToCartWithSession(pickIds, cartId, session);
      await session.commitTransaction();
      session.endSession();
      return result;
    } catch (error) {
      console.error("Error adding picks from carts:", error);
      throw new ErrorDomain("Error adding picks from carts", 500);
    }
  }

  /**
   * Remove picks from their original carts.
   * @param pickIds - The pick IDs to remove.
   * @param cartId - The cart ID from remove.
   * @returns A promise that resolves to an array of removed pick IDs or null.
   */
  public async deleteFromCart(
    pickIds: string[],
    cartId: string
  ): Promise<string[] | null> {
    const session = await CartModel.startSession();
    session.startTransaction();
    try {
      const result = await this.deleteFromCartWithSession(
        pickIds,
        cartId,
        session
      );
      await session.commitTransaction();
      session.endSession();
      return result;
    } catch (error) {
      console.error("Error deleting picks from carts:", error);
      throw new ErrorDomain("Error deleting picks from carts", 500);
    }
  }

  public async findById(id: string): Promise<CartDomain> {
    try {
      const cart = await CartModel.findOne({ _id: id }).exec();

      if (!cart) {
        console.error("Not found cart by cartId");
        throw new ErrorDomain("Not found cart by cartId", 404);
      }
      const cartDomain = CartPersistenceMapper.toDomain(cart!);

      return cartDomain;
    } catch (error) {
      console.error("Error finding cart by cartId:", error);
      throw new ErrorDomain("Error finding cart by cartId", 404);
    }
  }
}

export { CartRepository };
