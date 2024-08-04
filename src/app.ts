import "module-alias/register";

import express from "express";
import bodyParser from "body-parser";
import { CartRouter } from "./controllers/cart";
import { PickRouter } from "./controllers/pick";

import errorHandler from "./controllers/error.handler";
import { UserRouter } from "./controllers/user";
import { LoginRouter } from "./controllers/auth";
import cors from "cors";
const app = express();
const port = 3000;

app.use(cors());
app.use(bodyParser.json());

app.use("/carts", CartRouter);
app.use("/picks", PickRouter);
app.use("/users", UserRouter);
app.use("/login", LoginRouter);

app.use(errorHandler);

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
