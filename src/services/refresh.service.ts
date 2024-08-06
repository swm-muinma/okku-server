import { RefreshRepository } from "@src/adapters/persistence/repository/refresh.repository";
import { ErrorDomain } from "@src/domain/error.domain";
import { FormEnum } from "@src/enum/form.enum";
import jwt from "jsonwebtoken";
import {
  expirationTime,
  refreshTokenExpirationTime,
  secretKey,
} from "@src/infra/jwt.config";

const refreshRepository = new RefreshRepository();
export class RefreshService {
  async updateRefresh(refreshToken: string): Promise<{
    accessToken: string;
    refreshToken: string;
  }> {
    const isExist = await refreshRepository.isExist(refreshToken);
    if (!isExist) {
      throw new ErrorDomain("Not Valid refreshToken", 400);
    }
    try {
      const decoded = jwt.verify(refreshToken, secretKey) as { userId: string };
      const userId = decoded.userId;
      console.log(decoded.userId);
      const newAccessToken = jwt.sign({ userId: userId }, secretKey, {
        expiresIn: expirationTime,
      });
      const newRefreshToken = jwt.sign({ userId: userId }, secretKey, {
        expiresIn: refreshTokenExpirationTime,
      });
      await refreshRepository.delete(refreshToken);
      await refreshRepository.create(newRefreshToken);
      return {
        accessToken: newAccessToken,
        refreshToken: newRefreshToken,
      };
    } catch (err) {
      throw new ErrorDomain("Not Valid refreshToken", 400);
    }
  }
}
