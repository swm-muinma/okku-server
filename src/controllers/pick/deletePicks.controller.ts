import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";

const pickService = new PickService();

export const deletePicksController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const pickIds: string[] = req.body.pickIds;
  const cartId: string | null = req.body.cartId;
  const isDeletePermenant: boolean = req.body.isDeletePermenant;
  console.log("call deletePicks");
  try {
    const userId: string = req.user?.id!.toString()!;
    res
      .status(200)
      .send(
        await pickService.deletePicks(
          userId,
          pickIds,
          cartId,
          isDeletePermenant
        )
      );
  } catch (error) {
    return next(error);
  }
};
