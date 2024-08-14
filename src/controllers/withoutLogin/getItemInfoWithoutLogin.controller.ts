import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";
import { ReviewService } from "@src/services/review.service";
import { LogRepository } from "@src/adapters/persistence/repository/log.repository";

const logRepository = new LogRepository();
const reviewService = new ReviewService();

export const getItemInfoWithoutLoginViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const url: string = req.body.url;
  const okkuId: string = req.body.okkuId;
  const ip = req.headers["x-forwarded-for"] || req.socket.remoteAddress;
  const userAgentString: string = req.headers["user-agent"]!;
  console.log("call getItem Without login");
  try {
    const result = await reviewService.getItemInfoWithoutLogin(url, okkuId);

    await logRepository.create(
      "/scrape",
      req.body,
      result,
      userAgentString,
      ip!.toString(),
      false
    );

    res.status(200).send();
  } catch (error) {
    await logRepository.create(
      "/scrape",
      req.body,
      error,
      userAgentString,
      ip!.toString(),
      true
    );
    return next(error);
  }
};
