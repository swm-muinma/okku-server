import { Request, Response } from "express";
import { CartsService } from "../../services/cart.service";

const cartsService = new CartsService();

export const getMyCartsController = (req: Request, res: Response) => {
  res.status(200).send(cartsService.getMyCarts());
};
