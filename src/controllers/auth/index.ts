import { Router } from "express";
import { oauth2loginController } from "./oauth2.controller";
import { loginController } from "./login.controller";
import { kakaoLoginWithTokenController } from "./kakaoLoginWithToken.controller";
import { refreshController } from "./refresh.controller";

const router = Router();

router.get("/oauth2/code/:code", oauth2loginController);
router.get("/code/:code", loginController);
router.post("/app/kakao", kakaoLoginWithTokenController);
router.post("/refresh", refreshController);

export { router as LoginRouter };