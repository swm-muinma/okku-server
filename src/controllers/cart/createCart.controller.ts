import { Request, Response, NextFunction } from "express";
import { CartDomain } from "src/domain/cart.domain";
import { CartService } from "src/services/cart.service";

const cartsService = new CartService();
const userId = "66a99612385174b0b9a399a6";
export const createCartController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const name = req.body.name;
  const pickIds = req.body.pickIds;
  console.log("call createCart");
  try {
    const savedCart: CartDomain | null = await cartsService.createCart(
      userId,
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
