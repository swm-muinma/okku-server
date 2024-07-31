import { Request, Response, NextFunction } from "express";
import { UserService } from "src/services/user.service";

const userService = new UserService();
const userId = "66a99612385174b0b9a399a6";
export const getProfileController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    res.status(200).send(await userService.getProfile(userId));
    return;
  } catch (error) {
    return next(error);
  }
};
