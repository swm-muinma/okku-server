import { Router } from "express";
import { getProfileController } from "./getProfile.controller";
import { updateProfileController } from "./updateProfile.controller";
import { withdrawAccountController } from "./withdrawAccount.controller";

const router = Router();

router.get("/", getProfileController);
router.patch("/", updateProfileController);
router.get("/withdraw", withdrawAccountController);

export { router as UserRouter };
