import { ScraperAdapter } from "@src/adapters/crawlling/scraper.adapter";
import { SummarizeReviewAdapter } from "@src/adapters/crawlling/summarizeReview.adapter";
import { PickRepository } from "@src/adapters/persistence/repository/pick.repository";
import { ErrorDomain } from "@src/domain/error.domain";
import { ProductReviewDTO } from "@src/dto/summarizedReviews.dto";

const scraperAdapter = new ScraperAdapter();
const summarizeReviewAdapter = new SummarizeReviewAdapter();
const pickRepository = new PickRepository();

const okkuIds: string[] = [];

export class ReviewService {
  async getReviewsWithoutLogin(
    url: string,
    okkuId: string
  ): Promise<ProductReviewDTO> {
    try {
      const scrapedData = await scraperAdapter.scrape(url);
      console.log("scrape: ", scrapedData);

      let res = await summarizeReviewAdapter.getReviews();

      res.pick = {
        image: scrapedData.thumbnail_url,
        name: scrapedData.name,
        price: scrapedData.price,
        url: url,
      };

      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      okkuIds.push(okkuId);
      return res;
    } catch (err) {
      if (okkuIds.includes(okkuId)) {
        throw new ErrorDomain("must login", 402);
      }
      throw new ErrorDomain("error with scrape", 500);
    }
  }

  async getReviews(pickId: string): Promise<ProductReviewDTO> {
    try {
      const pick = await pickRepository.findById(pickId);
      let res = await summarizeReviewAdapter.getReviews();
      res.pick = pick;
      return res;
    } catch (err) {
      console.log(err);
      throw new ErrorDomain("error with scrape", 500);
    }
  }
}
