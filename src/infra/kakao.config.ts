import * as dotenv from "dotenv";

dotenv.config();

export const kakaoRedirectUri: string =
  process.env.BASE_REDIRECT_URI + "kakao" || "";

export const kakaoClientId: string = process.env.KAKAO_CLIENT_ID || "";

export const kakaoLoginUrl = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${kakaoClientId}&redirect_uri=${kakaoRedirectUri}`;
