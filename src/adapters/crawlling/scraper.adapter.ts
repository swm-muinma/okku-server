import { scraperUrl } from "@src/infra/scraper.config";
import axios from "axios";

interface ScrapedData {
  name: string;
  price: number;
  thumbnail_url: string;
  task_id: string;
  product_pk: string;
  platform: string;
}

class ScraperAdapter {
  private readonly scraperUrl: string;

  constructor() {
    this.scraperUrl = scraperUrl;
  }
  findPlatform(url: string): string {
    const platforms = ["musinsa", "zigzag", "a-bly"];

    // 각 플랫폼 문자열의 위치를 찾음. 없는 경우엔 Infinity로 설정
    const positions = platforms.map((platform) =>
      url.indexOf(platform) !== -1 ? url.indexOf(platform) : Infinity
    );

    // 가장 앞에 있는 플랫폼의 인덱스를 찾음
    const minPosition = Math.min(...positions);

    // 만약 모든 플랫폼이 없다면 null 반환
    if (minPosition === Infinity) {
      return "";
    }

    // 가장 앞에 있는 플랫폼을 반환
    return platforms[positions.indexOf(minPosition)];
  }

  async scrape(url: string): Promise<ScrapedData> {
    try {
      const response = await axios.post(this.scraperUrl, { path: url });
      response.data.platform = this.findPlatform(url);
      return response.data;
    } catch (error: any) {
      throw new Error(`Failed to scrape data: ${error.message}`);
    }
  }
}

export { ScraperAdapter };
