import { Router } from "express";
import { getReviewWithoutLoginViewController } from "./getReviewsWithoutLogin.controller";
import { getItemInfoWithoutLoginViewController } from "./getItemInfoWithoutLogin.controller";

const router = Router();

router.post("/reviews", getReviewWithoutLoginViewController);
router.post("/scrape", getItemInfoWithoutLoginViewController);

export { router as WithoutLoginRouter };
