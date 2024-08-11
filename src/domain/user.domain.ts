import { FormEnum } from "@src/enum/form.enum";

export class UserDomain {
  id: string | null;
  createdAt: Date | null;
  updatedAt: Date | null;
  kakaoId: string;
  appleId: string;
  isPremium: boolean;

  constructor(
    public name: string,
    public image: string,
    public height: number,
    public weight: number,
    public form: FormEnum
  ) {
    this.id = null;
    this.createdAt = null;
    this.updatedAt = null;
    this.kakaoId = "";
    this.appleId = "";
    this.isPremium = false;
  }
}
