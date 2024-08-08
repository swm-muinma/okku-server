import { scraperUrl } from "@src/infra/scraper.config";
import axios from "axios";

interface ScrapedData {
  name: string;
  price: number;
  thumbnail_url: string;
  task_id: string;
  product_pk: string;
}

class ScraperAdapter {
  private readonly scraperUrl: string;

  constructor() {
    this.scraperUrl = scraperUrl;
  }

  async scrape(url: string): Promise<ScrapedData> {
    try {
      const response = await axios.post(this.scraperUrl, { path: url });
      return response.data;
    } catch (error: any) {
      throw new Error(`Failed to scrape data: ${error.message}`);
    }
  }
}

export { ScraperAdapter };
