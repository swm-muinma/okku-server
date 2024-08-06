import { NextFunction, Request, Response } from "express";
import { PickService } from "@src/services/pick.service";

const pickService = new PickService();

export const createPickController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const url = req.body.url;
  try {
    const userId: string = req.user?.id!.toString()!;
    res.status(200).send(await pickService.createPick(userId, url));
  } catch (error) {
    return next(error);
  }
};
