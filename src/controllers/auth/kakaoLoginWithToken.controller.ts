import { Request, Response, NextFunction } from "express";
import { Oauth2Service } from "../../services/oauth2.service";
import { ErrorDomain } from "@src/domain/error.domain";

const oauth2Service = new Oauth2Service();

export const kakaoLoginWithTokenController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const token = req.body.token;
  const recomend = req.body.recomend;
  console.log("call login with kakaotalk");
  try {
    if (token == null) {
      throw new ErrorDomain("'token' is required", 400);
    }

    return res
      .status(200)
      .json(await oauth2Service.kakaoLoginWithToken(token, recomend));
  } catch (err) {
    console.log("Err", err);
    return next(err);
  }
};
