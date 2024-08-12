export class ReviewInsightDomain {
  constructor(
    public id: string,
    public platform: string,
    public product_pk: string,
    public cautions: ReviewSummaryDomain[],
    public positives: ReviewSummaryDomain[]
  ) {}
}

export class ReviewSummaryDomain {
  constructor(public description: string, public reviewIds: string[]) {}
}
