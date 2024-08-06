import * as dotenv from "dotenv";

dotenv.config();

export const secretKey: string = process.env.JWT_SECRET || "";
export const expirationTime: string = process.env.EXPIRATION_TIME || "";
export const refreshTokenExpirationTime: string =
  process.env.REFRESHTOKEN_EXPIRATION_TIME || "";
