import * as dotenv from "dotenv";

dotenv.config();

const appleRedirectUri: string = process.env.BASE_REDIRECT_URI + "apple" || "";
const appleClientId: string = process.env.APPLE_CLIENT_ID || "";
const appleTeamId: string = process.env.APPLE_TEAM_ID || "";
const appleKeyId: string = process.env.APPLE_KEY_ID || "";
const appleScope: string = process.env.APPLE_SCOPE || "";
const appleKeyFilePath: string = process.env.APPLE_KEY_FILE_PATH || "";

export const appleConfig = {
  client_id: appleClientId,
  team_id: appleTeamId,
  key_id: appleKeyId,
  redirect_uri: appleRedirectUri,
  scope: appleScope,
};

export const appleLoginUrl = `https://appleid.apple.com/auth/authorize?client_id=${appleClientId}&redirect_uri=${appleRedirectUri}&response_type=code`;

export const keyFilePath = appleKeyFilePath;
