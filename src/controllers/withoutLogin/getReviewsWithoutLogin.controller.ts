import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";

const pickService = new PickService();

export const getReviewWithoutLoginViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const url: string = req.body.url;
  console.log("call getComparisonView");
  console.log(req);
  try {
    res.status(200).send(pickService.getReviewsWithoutLogin(url));
  } catch (error) {
    return next(error);
  }
};
