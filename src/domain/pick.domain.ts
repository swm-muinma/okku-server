export class PickDomain {
  id: string | null;
  createdAt: Date | null;
  updatedAt: Date | null;

  constructor(
    public userId: string,
    public name: string,
    public price: number,
    public image: string,
    public platform: PlatformDomain
  ) {
    this.id = null;
    this.createdAt = null;
    this.updatedAt = null;
  }
}

export class PlatformDomain {
  constructor(public name: string, public image: string, public url: string) {}
}
