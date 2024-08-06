import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import { secretKey } from "@src/infra/jwt.config";
import { ErrorDomain } from "@src/domain/error.domain";
import { UserRepository } from "@src/adapters/persistence/repository/user.repository";

const userRepository = new UserRepository();

export const authenticateJWT = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers.authorization;

  if (authHeader) {
    const token = authHeader.split(" ")[1];

    try {
      const decoded = jwt.verify(token, secretKey) as { userId: string };
      const user = await userRepository.getById(decoded.userId);

      if (!user) {
        throw new ErrorDomain("User not found", 404);
      }

      req.user = user;
      next();
    } catch (err) {
      console.error("JWT validation error:", err);
      return res.status(401).json({ message: "Invalid token" });
    }
  } else {
    return res.status(401).json({ message: "Authorization header missing" });
  }
};
