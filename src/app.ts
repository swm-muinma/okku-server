import "module-alias/register";

import express from "express";
import bodyParser from "body-parser";
import { CartRouter } from "./controllers/cart";
import { PickRouter } from "./controllers/pick";

import errorHandler from "./infra/middleware/error.handler";
import { UserRouter } from "./controllers/user";
import { LoginRouter } from "./controllers/auth";
import cors from "cors";
import { authenticateJWT } from "./infra/middleware/jwt.authenticate";

import { UserDomain } from "@src/domain/user.domain";
import { WithoutLoginRouter } from "./controllers/withoutLogin";

declare global {
  namespace Express {
    interface Request {
      user?: UserDomain;
    }
  }
}

const app = express();
const port = 80;

app.use(cors());
app.use(bodyParser.json());

app.use("/carts", authenticateJWT, CartRouter);
app.use("/picks", authenticateJWT, PickRouter);
app.use("/users", authenticateJWT, UserRouter);
app.use("/login", LoginRouter);
app.use("/", WithoutLoginRouter);

app.use(errorHandler);

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
