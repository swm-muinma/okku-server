import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";

const pickService = new PickService();
export const getReviewsController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const pickId: string = req.params.pickId;
  console.log("call getReviews");
  try {
    res.status(200).send(await pickService.getReviews(pickId));
  } catch (error) {
    return next(error);
  }
};
