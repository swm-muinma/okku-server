import { Router } from "express";
import { getReviewWithoutLoginViewController } from "./getReviewsWithoutLogin.controller";
import { getItemInfoWithoutLoginViewController } from "./getItemInfoWithoutLogin.controller";
import { healthyController } from "./healthy.controller";

const router = Router();

router.post("/reviews", getReviewWithoutLoginViewController);
router.post("/scrape", getItemInfoWithoutLoginViewController);
router.get("/healthy", healthyController);

export { router as WithoutLoginRouter };
