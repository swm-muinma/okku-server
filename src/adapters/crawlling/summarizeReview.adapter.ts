const dummt = {
  pick: {
    image:
      "https://d3ha2047wt6x28.cloudfront.net/wfDbYoyS7LA/pr:GOODS_DETAIL/czM6Ly9hYmx5LWltYWdlLWxlZ2FjeS9kYXRhL2dvb2RzLzIwMjQwODAxXzE3MjI0NzYzNTI3MTAzMjhtLmpwZw",
    name: "무센트 슬로건 반팔 티 커플룩 시밀러룩 커플티 (크롭, 루즈핏)",
    price: 29900,
  },
  cons: [
    {
      content: "옷이 클 수도 있어요!",
      count: 19,
      comments: [
        {
          name: "홍길동",
          height: 75,
          weight: 70,
          comment: "옷이 생각보다 많이 컸어요.",
          image: null,
        },
        {
          name: "김철수",
          height: 180,
          weight: 75,
          comment: "클 수 있지만 괜찮습니다.",
          image:
            "https://d3ha2047wt6x28.cloudfront.net/wfDbYoyS7LA/pr:GOODS_DETAIL/czM6Ly9hYmx5LWltYWdlLWxlZ2FjeS9kYXRhL2dvb2RzLzIwMjQwODAxXzE3MjI0NzYzNTI3MTAzMjhtLmpwZw",
        },
      ],
    },
    {
      content: "소매가 짧을 수 있어요!",
      count: 10,
      details: [
        {
          name: "홍길동",
          height: 175,
          weight: 70,
          comment: "옷이 생각보다 많이 컸어요.",
          image: null,
        },
        {
          name: "김철수",
          height: 180,
          weight: 75,
          comment: "클 수 있지만 괜찮습니다.",
          image:
            "https://d3ha2047wt6x28.cloudfront.net/wfDbYoyS7LA/pr:GOODS_DETAIL/czM6Ly9hYmx5LWltYWdlLWxlZ2FjeS9kYXRhL2dvb2RzLzIwMjQwODAxXzE3MjI0NzYzNTI3MTAzMjhtLmpwZw",
        },
      ],
    },
    // 추가 리뷰 항목들...
  ],
  pros: [
    {
      content: "옷이 잘 맞아요!",
      count: 15,
      comments: [
        {
          name: "이영희",
          height: 160,
          weight: 50,
          comment: "옷이 딱 맞고 편해요!",
          image:
            "https://d3ha2047wt6x28.cloudfront.net/wfDbYoyS7LA/pr:GOODS_DETAIL/czM6Ly9hYmx5LWltYWdlLWxlZ2FjeS9kYXRhL2dvb2RzLzIwMjQwODAxXzE3MjI0NzYzNTI3MTAzMjhtLmpwZw",
        },
        {
          name: "박지훈",
          height: 170,
          weight: 65,
          comment: "착용감이 좋고 사이즈가 적당해요.",
          image: null,
        },
      ],
    },
    // 추가 리뷰 항목들...
  ],
};

class SummarizeReviewAdapter {
  getReviews(): any {
    return dummt;
  }
}

export { SummarizeReviewAdapter };
