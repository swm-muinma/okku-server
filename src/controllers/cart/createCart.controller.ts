import { Request, Response, NextFunction } from "express";
import { CartDomain } from "src/domain/cart.domain";
import { CartService } from "src/services/cart.service";

const cartsService = new CartService();

export const createCartController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const name = req.body.name;
  const pickIds = req.body.pickIds;
  try {
    const savedCart: CartDomain | null = await cartsService.createCart(
      name,
      pickIds
    );
    res.status(200).send({
      id: savedCart.id,
      name: savedCart.name,
      pickIds: savedCart.pickItemIds,
    });
    return;
  } catch (error) {
    return next(error);
  }
};
