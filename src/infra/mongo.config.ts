import mongoose, { Document, Schema, Model } from "mongoose";
import * as dotenv from "dotenv";

dotenv.config();

const mongoDbURI = process.env.MONGODB_URI || "";

mongoose
  .connect(mongoDbURI)
  .then(() => {
    console.log("Connected to MongoDB");
  })
  .catch((error) => {
    console.error("Error connecting to MongoDB:", error);
    process.exit(1);
  });

const db = mongoose.connection;
db.on("error", console.error.bind(console, "MongoDB connection error:"));
db.once("open", () => {
  console.log("Connected to MongoDB");
});

export const createModel = <T extends Document>(
  name: string,
  schema: Schema<T>
): Model<T> => {
  return mongoose.model<T>(name, schema);
};
