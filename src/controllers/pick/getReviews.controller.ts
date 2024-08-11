import { NextFunction, Request, Response } from "express";
import { ErrorDomain } from "@src/domain/error.domain";
import { ReviewService } from "@src/services/review.service";

const reviewService = new ReviewService();

export const getReviewsController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.log("call getReviews");
  try {
    const pickId: string | null = req.query.pickId?.toString()!;
    if (!pickId) {
      throw new ErrorDomain("invalid pickId", 400);
    }
    console.log(pickId);
    res.status(200).send(await reviewService.getReviews(pickId));
  } catch (error) {
    return next(error);
  }
};
