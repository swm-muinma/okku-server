import { NextFunction, Request, Response } from "express";
import { CartService } from "@src/services/cart.service";

const cartsService = new CartService();

export const deleteCartController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const cartId = req.params.id;
  console.log("call deleteCart");
  try {
    const userId: string = req.user?.id!.toString()!;
    const deletedCartId = await cartsService.deleteCart(userId, cartId);
    res.status(200).send({ cartId: deletedCartId });
  } catch (error) {
    return next(error);
  }
};
