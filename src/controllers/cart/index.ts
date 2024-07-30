import { Router } from "express";
import { getMyCartsController } from "./getMyCarts.controller";
import { createCartController } from "./createCart.controller";
import { deleteCartController } from "./deleteCarts.controller";

const router = Router();

router.get("/", getMyCartsController);
router.post("/", createCartController);
router.delete("/:id", deleteCartController);

export { router };
