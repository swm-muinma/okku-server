import { NextFunction, Request, Response } from "express";
import { CartService } from "src/services/cart.service";

const cartsService = new CartService();
const userId = "test_user_id";
export const getMyCartsController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const page = parseInt(req.query.page as string, 10) || 1;
  const size = parseInt(req.query.size as string, 10) || 10;
  try {
    const carts = await cartsService.getMyCarts(userId, page, size);
    res.status(200).send(carts);
  } catch (error) {
    return next(error);
  }
};
