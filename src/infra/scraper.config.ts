import * as dotenv from "dotenv";

dotenv.config();

export const scraperUrl: string = process.env.SCRAPER_URL || "";
