import mongoose, { Document, Schema, Model, Connection } from "mongoose";
import * as dotenv from "dotenv";
mongoose.set("debug", true);
dotenv.config();

const mainDbURI = process.env.MONGODB_URI_MAIN || "";
const reviewInsightDbURI = process.env.MONGODB_URI_REVIEW_INSIGHT || "";
const reviewDbURI = process.env.MONGODB_URI_REVIEW || "";

const connectToDatabase = (uri: string, dbName: string): Connection => {
  const connection = mongoose.createConnection(uri);

  connection.on("connected", () => {
    console.log(`Connected to ${dbName} Database`);
  });

  connection.on("error", (error) => {
    console.error(`${dbName} Database connection error:`, error);
    process.exit(1); // Connection 실패 시, 프로세스 종료
  });

  return connection;
};

export const mainConnection = connectToDatabase(mainDbURI, "Main");
export const reviewInsightConnection = connectToDatabase(
  reviewInsightDbURI,
  "Review_Insight"
);
export const reviewConnection = connectToDatabase(reviewDbURI, "Review");

export const createModel = <T extends Document>(
  name: string,
  schema: Schema<T>,
  connection: Connection
): Model<T> => {
  return connection.model<T>(name, schema);
};
