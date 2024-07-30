import { NextFunction, Request, Response } from "express";
import { PickService } from "src/services/pick.service";

const pickService = new PickService();
export const getComparisonViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    res.status(200).send(pickService.getComparisonView());
  } catch (error) {
    return next(error);
  }
};
