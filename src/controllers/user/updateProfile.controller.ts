import { Request, Response, NextFunction } from "express";
import { FormEnum } from "@src/enum/form.enum";
import { UserService } from "@src/services/user.service";

const userService = new UserService();
const userId = "66a99612385174b0b9a399a6";
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
