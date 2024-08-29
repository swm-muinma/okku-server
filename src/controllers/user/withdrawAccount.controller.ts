import { Request, Response, NextFunction } from "express";
import { FormEnum } from "@src/enum/form.enum";
import { UserService } from "@src/services/user.service";

const userService = new UserService();

export const withdrawAccountController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.log("call withdrawAccount");
  try {
    const userId: string = req.user?.id!.toString()!;
    res.status(200).send(await userService.withdrawAccount(userId));
    return;
  } catch (error) {
    return next(error);
  }
};
