import { Router } from "express";
import { getProfileController } from "./getProfile.controller";
import { updateProfileController } from "./updateProfile.controller";
import { loginController } from "./login.controller";

const router = Router();

router.get("/", getProfileController);
router.patch("/", updateProfileController);
router.post("/login", loginController);

export { router as UserRouter };
