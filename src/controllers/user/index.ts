import { Router } from "express";
import { getProfileController } from "./getProfile.controller";
import { updateProfileController } from "./updateProfile.controller";

const router = Router();

router.get("/", getProfileController);
router.patch("/", updateProfileController);

export { router as UserRouter };
