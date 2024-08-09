import { Router } from "express";
import { getReviewWithoutLoginViewController } from "./getReviewsWithoutLogin.controller";

const router = Router();

router.post("/reviews", getReviewWithoutLoginViewController);

export { router as WithoutLoginRouter };
