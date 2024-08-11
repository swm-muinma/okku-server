export class SummarizedReviewsDomain {
  constructor(
    public productPk: string,
    public cautions: SummarizedReviewDomain[],
    public positives: SummarizedReviewDomain[]
  ) {}
}

export class SummarizedReviewDomain {
  constructor(public description: string, public reviewIds: string[]) {}
}
