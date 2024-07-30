import { NextFunction, Request, Response } from "express";
import { PickService } from "src/services/pick.service";

const pickService = new PickService();
export const getComparisonViewController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const pickIds = req.body.pickIds;
  try {
    res.status(200).send(await pickService.getComparisonView(pickIds));
  } catch (error) {
    return next(error);
  }
};
