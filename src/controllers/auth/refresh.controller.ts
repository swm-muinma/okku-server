import { Request, Response, NextFunction } from "express";
import { Oauth2Service } from "../../services/oauth2.service";
import { ErrorDomain } from "@src/domain/error.domain";
import { RefreshService } from "@src/services/refresh.service";

const refreshService = new RefreshService();

export const refreshController = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const refreshToken = req.body.refreshToken;
  console.log("call refresh");
  if (refreshToken == null) {
    throw new ErrorDomain("'refreshToken' is required", 400);
  }
  try {
    const result = await refreshService.updateRefresh(refreshToken);
    return res.status(200).json(result);
  } catch (err) {
    console.log("Err", err);
    return next(err);
  }
};
