import { NextFunction, Request, Response } from "express";
import { PickService } from "src/services/pick.service";

const pickService = new PickService();
const userId = "66a99612385174b0b9a399a6";
export const createPickController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const url = req.body.url;
  console.log("call createPick");
  try {
    res.status(200).send(await pickService.createPick(userId, url));
  } catch (error) {
    return next(error);
  }
};
