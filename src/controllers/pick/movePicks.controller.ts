import { NextFunction, Request, Response } from "express";
import { CartService } from "src/services/cart.service";
import { PickService } from "src/services/pick.service";

const cartService = new CartService();

export const movePicksController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const pickIds = req.body.pickIds;
  const cartId = req.body.cartId;
  const isDeleteFromOrigin = req.body.isDeleteFromOrigin;
  try {
    res
      .status(200)
      .send(cartService.movePicks(pickIds, cartId, isDeleteFromOrigin));
  } catch (error) {
    return next(error);
  }
};
