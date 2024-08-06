import { Document, Schema, model } from "mongoose";
// UserEntity 클래스
class RefreshEntity {
  _id?: string;

  constructor(public refreshToken: string) {}
}

// Mongoose 스키마
const UserSchema: Schema<RefreshEntity & Document> = new Schema({
  refreshToken: { type: String, required: true },
});

const RefreshModel = model<RefreshEntity & Document>("Refresh", UserSchema);

export { RefreshModel, RefreshEntity };
