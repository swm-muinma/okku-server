import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";
import { ReviewService } from "@src/services/review.service";

const reviewService = new ReviewService();

export const getReviewWithoutLoginViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const url: string = req.body.url;
  const okkuId: string = req.body.okkuId;
  console.log("call getReviews Without login");
  try {
    res
      .status(200)
      .send(await reviewService.getReviewsWithoutLogin(url, okkuId));
  } catch (error) {
    return next(error);
  }
};
