import { Router } from "express";
import { createPickController } from "./createPick.controller";
import { deletePicksController } from "./deletePicks.controller";
import { getReviewsController } from "./getReviews.controller";
import { getMyPicksController } from "./getMyPicks.controller";
import { movePicksController } from "./movePicks.controller";

const router = Router();

router.post("/new", createPickController);
router.post("/delete", deletePicksController);
router.get("/reviews", getReviewsController);
router.get("/", getMyPicksController);
router.patch("/", movePicksController);

export { router as PickRouter };
