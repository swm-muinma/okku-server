import { Request, Response, NextFunction } from "express";
import { FormEnum } from "@src/enum/form.enum";
import { UserService } from "@src/services/user.service";

const userService = new UserService();

export const updateProfileController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const name = req.body.name;
  const height = parseInt(req.body.height as string, 10) || undefined;
  const weight = parseInt(req.body.weight as string, 10) || undefined;
  const form = req.body.form as FormEnum;
  console.log("call updateProfile");
  try {
    const userId: string = req.user?.id!.toString()!;
    res
      .status(200)
      .send(
        await userService.updateProfile(userId, name, height, weight, form)
      );
    return;
  } catch (error) {
    return next(error);
  }
};
