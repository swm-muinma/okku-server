import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";

const pickService = new PickService();
export const getMyPicksController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const page = parseInt(req.query.page as string, 10) || 1;
  const size = parseInt(req.query.size as string, 10) || 10;
  const cartId = req.query.cartId?.toString();
  console.log("call getMyPicks");
  try {
    const userId: string = req.user?.id!.toString()!;
    res
      .status(200)
      .send(await pickService.getMyPicks(userId, cartId!, page, size));
  } catch (error) {
    return next(error);
  }
};
