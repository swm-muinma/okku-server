import express from "express";
import bodyParser from "body-parser";
import { router as CartRouter } from "./controllers/cart";
import errorHandler from "./controllers/error.handler";

const app = express();
const port = 3000;

app.use(bodyParser.json());
app.use("/carts", CartRouter);

app.use(errorHandler);

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
