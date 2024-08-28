import { NextFunction, Request, Response } from "express";

export const healthyController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    res.status(200).send();
  } catch (error) {
    return next(error);
  }
};
