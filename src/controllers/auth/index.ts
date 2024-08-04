import { Router } from "express";
import { oauth2loginController } from "./oauth2.controller";
import { loginController } from "./login.controller";

const router = Router();

router.get("/oauth2/code/:code", oauth2loginController);
router.get("/code/:code", loginController);

export { router as LoginRouter };
