import { Router } from "express";
import { getMyCartsController } from "./getMyCarts.controller";

const router = Router();

router.get("/", getMyCartsController);

export { router };
