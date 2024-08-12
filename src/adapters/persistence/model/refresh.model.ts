import { createModel, mainConnection } from "@src/infra/mongo.config";
import { Document, Schema, model } from "mongoose";
// UserEntity 클래스
class RefreshEntity {
  _id?: string;

  constructor(public refreshToken: string) {}
}

// Mongoose 스키마
const RefreshSchema: Schema<RefreshEntity & Document> = new Schema({
  refreshToken: { type: String, required: true },
});

const RefreshModel = createModel<RefreshEntity & Document>(
  "Refresh",
  RefreshSchema,
  mainConnection
);

export { RefreshModel, RefreshEntity };
