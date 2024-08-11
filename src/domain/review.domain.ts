export class ReviewDomain {
  constructor(
    public id: string,
    public rating: string,
    public gender: string,
    public option: string,
    public criterion: any,
    public height: number,
    public weight: number,
    public topSize: string,
    public bottomSize: string,
    public content: string,
    public imageUrl: string,
    public productPk: string,
    public platform: string
  ) {}
}
