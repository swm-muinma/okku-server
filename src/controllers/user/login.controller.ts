import { Request, Response, NextFunction } from "express";
import { UserService } from "src/services/user.service";

const userService = new UserService();
export const loginController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    res.status(200).send(await userService.login());
    return;
  } catch (error) {
    return next(error);
  }
};
