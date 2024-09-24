import AppleAuth from "apple-auth";
import jwt from "jsonwebtoken";
import {
  appleConfig,
  appleLoginUrl,
  keyFilePath,
} from "@src/infra/apple.config";
import fs from "fs";
import { ErrorDomain } from "@src/domain/error.domain";
import { UserRepository } from "@src/adapters/persistence/repository/user.repository";
import axios from "axios";
import jwkToPem from "jwk-to-pem";
import {
  kakaoClientId,
  kakaoLoginUrl,
  kakaoRedirectUri,
} from "@src/infra/kakao.config";
import {
  expirationTime,
  refreshTokenExpirationTime,
  secretKey,
} from "@src/infra/jwt.config";
import { UserDomain } from "@src/domain/user.domain";
import { FormEnum } from "@src/enum/form.enum";
import { RefreshRepository } from "@src/adapters/persistence/repository/refresh.repository";

const userRepository = new UserRepository();
const refreshRepository = new RefreshRepository();

export class Oauth2Service {
  getRedirect(platform: string): string {
    switch (platform) {
      case "apple":
        return appleLoginUrl;
      case "kakao":
        return kakaoLoginUrl;
      default:
        throw new ErrorDomain("Unsupported platform", 400);
    }
  }

  async oauth2login(
    platform: string,
    authorizationCode: string
  ): Promise<{
    accessToken: string;
    refreshToken: string;
    isNewUser: boolean;
  }> {
    switch (platform) {
      case "apple":
        return await this.handleAppleLogin(authorizationCode);
      case "kakao":
        return await this.handleKakaoLogin(authorizationCode);
      default:
        throw new ErrorDomain("Unsupported platform", 400);
    }
  }

  private async handleAppleLogin(authorizationCode: string): Promise<{
    accessToken: string;
    refreshToken: string;
    isNewUser: boolean;
  }> {
    const keyPath = process.env.AUTH_KEY_PATH;
    if (!keyPath) {
      throw new Error("Environment variable  is not set");
    }
    const privateKey = fs.readFileSync(keyPath, "utf8");
    const appleAuth = new AppleAuth(appleConfig, privateKey, "text");
    let isNewUser = false;
    try {
      const response = await appleAuth.accessToken(authorizationCode);
      const idToken = jwt.decode(response.id_token, { complete: true });
      const publicKey = await this.getApplePublicKey(idToken!.header.kid);
      const verifiedToken = jwt.verify(response.id_token, publicKey, {
        algorithms: ["RS256"],
      });

      const appleId = verifiedToken.sub?.toString();
      if (!appleId) {
        throw new ErrorDomain("appleId not found from apple", 404);
      }

      let user = await userRepository.getByAppleId(appleId);

      if (!user) {
        console.log("New user registration logic here");
        isNewUser = true;
        user = new UserDomain("유저", "", 0, 0, FormEnum.NORMAL);
        user.appleId = appleId;
        user = await userRepository.create(user);
      }

      const newAccessToken = jwt.sign({ userId: user.id }, secretKey, {
        expiresIn: expirationTime,
      });
      const refreshToken = jwt.sign({ userId: user.id }, secretKey, {
        expiresIn: refreshTokenExpirationTime,
      });
      await refreshRepository.create(refreshToken);

      return {
        accessToken: newAccessToken,
        refreshToken: refreshToken,
        isNewUser: isNewUser,
      };
    } catch (err) {
      console.error("Error during Apple login:", err);
      throw new ErrorDomain("Error during Apple login", 500);
    }
  }

  private async handleKakaoLogin(authorizationCode: string): Promise<{
    accessToken: string;
    refreshToken: string;
    isNewUser: boolean;
  }> {
    try {
      const tokenResponse = await axios.post(
        "https://kauth.kakao.com/oauth/token",
        null,
        {
          params: {
            grant_type: "authorization_code",
            client_id: kakaoClientId,
            redirect_uri: kakaoRedirectUri,
            code: authorizationCode,
            // client_secret: "YOUR_CLIENT_SECRET", // If you have a client secret
          },
        }
      );

      const accessToken = tokenResponse.data.access_token;

      return this.kakaoLoginWithToken(accessToken, "");
    } catch (err) {
      console.error("Error during Kakao login:", err);
      throw new ErrorDomain("err", 500);
    }
  }

  private async getApplePublicKey(kid: any) {
    const response = await axios.get("https://appleid.apple.com/auth/keys");
    const keys = response.data.keys;
    const key = keys.find((k: any) => k.kid === kid);
    return jwkToPem(key);
  }

  public async kakaoLoginWithToken(
    accessToken: string,
    recomend: string
  ): Promise<{
    accessToken: string;
    refreshToken: string;
    isNewUser: boolean;
  }> {
    console.log("recomend", recomend);
    let isNewUser = false;
    const userResponse = await axios.get("https://kapi.kakao.com/v2/user/me", {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      params: {
        property_keys: [
          "kakao_account.email",
          "kakao_account.profile.nickname",
        ],
      },
    });
    console.log("kakao login");
    console.log(userResponse.data.kakao_account);

    const kakaoId: string = userResponse.data.id.toString();

    if (!kakaoId) {
      throw new ErrorDomain("Kakao ID not found from Kakao", 404);
    }
    let user: UserDomain | null = await userRepository.getByKakaoId(kakaoId);
    if (!user) {
      user = new UserDomain(
        userResponse.data.kakao_account.profile.nickname,
        "",
        0,
        0,
        FormEnum.NORMAL
      );
      user.kakaoId = kakaoId;
      if (recomend != null && recomend != "") {
        await userRepository.updateToPremium(recomend);
      }
      user = await userRepository.create(user);
      isNewUser = true;
    }
    const newAccessToken = jwt.sign({ userId: user.id }, secretKey, {
      expiresIn: expirationTime,
    });
    const refreshToken = jwt.sign({ userId: user.id }, secretKey, {
      expiresIn: refreshTokenExpirationTime,
    });
    await refreshRepository.create(refreshToken);
    return {
      accessToken: newAccessToken,
      refreshToken: refreshToken,
      isNewUser: isNewUser,
    };
  }
}
