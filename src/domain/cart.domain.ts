export class CartDomain {
  id: string | null;
  createdAt: Date | null;
  updatedAt: Date | null;

  constructor(
    public userId: string,
    public name: string,
    public pickNum: number,
    public pickItemIds: string[],
    public pickImages: string[]
  ) {
    this.id = null;
    this.createdAt = null;
    this.updatedAt = null;
  }
}
