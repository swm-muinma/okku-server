import { NextFunction, Request, Response } from "express";
import { CartService } from "@src/services/cart.service";

const cartsService = new CartService();

export const getMyCartsController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const page = parseInt(req.query.page as string, 10) || 1;
  const size = parseInt(req.query.size as string, 10) || 10;
  console.log("call getMyCarts");
  try {
    const userId: string = req.user?.id!.toString()!;
    const carts = await cartsService.getMyCarts(userId, page, size);
    res.status(200).send(carts);
  } catch (error) {
    return next(error);
  }
};
