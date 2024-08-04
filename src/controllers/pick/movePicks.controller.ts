import { NextFunction, Request, Response } from "express";
import { CartService } from "@src/services/cart.service";

const cartsService = new CartService();

export const movePicksController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const pickIds: string[] = req.body.pickIds;
  const sourceCartId: string = req.body.sourceCartId;
  const destinationCartId: string = req.body.destinationCartId;
  const isDeleteFromOrigin: boolean = req.body.isDeleteFromOrigin;
  console.log("call movePicks");
  try {
    const result = await cartsService.movePicks(
      pickIds,
      sourceCartId,
      destinationCartId,
      isDeleteFromOrigin
    );
    res.status(200).send(result);
  } catch (error) {
    return next(error);
  }
};
