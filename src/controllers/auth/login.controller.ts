import { Request, Response, NextFunction } from "express";
import { Oauth2Service } from "../../services/oauth2.service";
import { ErrorDomain } from "@src/domain/error.domain";

const oauth2Service = new Oauth2Service();

export const loginController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const platform = req.params.code;
  console.log("call login");
  if (platform == null) {
    throw new ErrorDomain("'code' is required", 400);
  }
  try {
    const result = await oauth2Service.getRedirect(platform);
    if (req.body.user) {
      const { name } = JSON.parse(req.body.user);
      //   user.name = name; // name = { firstname: , lastname: }
      const username = name.lastname + name.firstname;
      console.log(req.body.user);
      console.log(username);
      //   const appleLoginUserInfo = await appleSign(id, username, email);
    }
    return res.status(200).json({ result });
  } catch (err) {
    console.log("Err", err);
    return next(err);
  }
};
