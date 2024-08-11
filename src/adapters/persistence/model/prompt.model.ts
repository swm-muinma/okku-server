import { Document, Schema, model } from "mongoose";
// UserEntity 클래스
class PromptEntity {
  _id?: string;

  constructor(public name: string, public prompt: string) {}
}

// Mongoose 스키마
const UserSchema: Schema<PromptEntity & Document> = new Schema({
  name: { type: String, required: true },
  prompt: { type: String, required: true },
});

const PromptModel = model<PromptEntity & Document>("Prompt", UserSchema);

export { PromptModel, PromptEntity };
