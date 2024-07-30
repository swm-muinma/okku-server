// src/middlewares/errorHandler.ts

import { Request, Response, NextFunction } from "express";
import { ErrorDomain } from "src/domain/error.domain";

const errorHandler = (
  err: Error | ErrorDomain,
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.error(err.stack);
  if (err instanceof ErrorDomain) {
    return res.status(err.statusCode).json({
      message: err.message,
      //   details: err.stack,
    });
  }
  return res.status(500).json({
    message: "Internal Server Error",
    // details: err.message,
  });
};

export default errorHandler;
