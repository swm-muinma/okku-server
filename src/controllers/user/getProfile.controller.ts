import { Request, Response, NextFunction } from "express";
import { UserService } from "@src/services/user.service";

const userService = new UserService();
export const getProfileController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.log("call getProfile");
  try {
    const userId: string = req.user?.id!.toString()!;
    res.status(200).send(await userService.getProfile(userId));
    return;
  } catch (error) {
    return next(error);
  }
};
