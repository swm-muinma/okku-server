import express from "express";
import bodyParser from "body-parser";
import { CartRouter } from "./controllers/cart";
import { PickRouter } from "./controllers/pick";

import errorHandler from "./controllers/error.handler";
import { UserRouter } from "./controllers/user";

const app = express();
const port = 3000;

app.use(bodyParser.json());
app.use("/carts", CartRouter);
app.use("/picks", PickRouter);
app.use("/users", UserRouter);

app.use(errorHandler);

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
