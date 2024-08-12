import { createModel, mainConnection } from "@src/infra/mongo.config";
import { Document, Schema, model } from "mongoose";
// UserEntity 클래스
class PromptEntity {
  _id?: string;

  constructor(public name: string, public prompt: string) {}
}

// Mongoose 스키마
const PromptSchema: Schema<PromptEntity & Document> = new Schema({
  name: { type: String, required: true },
  prompt: { type: String, required: true },
});

const PromptModel = createModel<PromptEntity & Document>(
  "Prompt",
  PromptSchema,
  mainConnection
);

export { PromptModel, PromptEntity };
