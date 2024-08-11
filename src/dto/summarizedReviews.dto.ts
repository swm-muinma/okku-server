// PickDTO 정의
export interface PickDTO {
  id?: string;
  image: string;
  name: string;
  price: number;
  url: string;
}

// CommentDTO 정의
export interface CommentDTO {
  name?: string;
  height?: number;
  weight?: number;
  comment: string;
  image?: string;
}

// ReviewSectionDTO 정의
export interface ReviewSectionDTO {
  content: string;
  count: number;
  comments: CommentDTO[];
}

// ReviewsDTO 정의
export interface ReviewsDTO {
  cons: ReviewSectionDTO[];
  pros: ReviewSectionDTO[];
}

// 메인 DTO 정의
export interface ProductReviewDTO {
  pick: PickDTO;
  reviews: ReviewsDTO;
}
