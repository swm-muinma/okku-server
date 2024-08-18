import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";
import { ReviewService } from "@src/services/review.service";
import { LogRepository } from "@src/adapters/persistence/repository/log.repository";

const reviewService = new ReviewService();
const logRepository = new LogRepository();

export const getReviewWithoutLoginViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const productPk: string = req.body.productPk;
  const platform: string = req.body.platform;
  const okkuId: string = req.body.okkuId;
  const ip = req.headers["x-forwarded-for"] || req.socket.remoteAddress;
  const userAgentString: string = req.headers["user-agent"]!;
  console.log("call getReviews Without login");
  try {
    const result = await reviewService.getReviewsWithoutLogin(
      productPk,
      platform,
      okkuId
    );

    await logRepository.create(
      "/reviews",
      req.body,
      result,
      userAgentString,
      ip!.toString(),
      false
    );
    res.status(200).send(result);
  } catch (error) {
    await logRepository.create(
      "/reviews",
      req.body,
      error,
      userAgentString,
      ip!.toString(),
      true
    );
    return next(error);
  }
};
