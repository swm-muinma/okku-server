import { createModel, mainConnection } from "@src/infra/mongo.config";
import { Document, Schema, model } from "mongoose";

class LogEntity {
  _id?: string;
  constructor(
    public api: string,
    public request: any,
    public response: any,
    public user_agent: string,
    public ip: string,
    public is_error: boolean
  ) {}
}

const LogSchema: Schema<LogEntity & Document> = new Schema({
  api: { type: String, required: true },
  request: { type: Object, required: false },
  response: { type: Object, required: false },
  user_agent: { type: String, required: false },
  ip: { type: String, required: false },
  is_error: { type: Boolean, required: true },
});

const LogModel = createModel<LogEntity & Document>(
  "Log",
  LogSchema,
  mainConnection
);

export { LogModel, LogEntity };
