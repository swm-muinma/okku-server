package kr.okku.server.adapters.scraper;

import kr.okku.server.domain.ScrapedDataDomain;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ScraperAdapter {

    private final RestTemplate restTemplate;
    private final String scraperUrl = "your-scraper-url";
    private final String checkrUrl = scraperUrl + "/status";

    public ScraperAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findPlatform(String url) {
        String[] platforms = {"musinsa", "zigzag", "a-bly"};
        int minPosition = Integer.MAX_VALUE;
        String platform = "";

        for (String p : platforms) {
            int position = url.indexOf(p);
            if (position != -1 && position < minPosition) {
                minPosition = position;
                platform = p;
            }
        }
        return platform;
    }

    public ScrapedDataDomain scrape(String url) {
        try {
            ScrapedDataDomain response = restTemplate.postForObject(scraperUrl + "/scrap", new ScrapeRequest(url), ScrapedDataDomain.class);
            if (response != null) {
                response.setPlatform(findPlatform(url));
            }
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public ScrapedDataDomain checkWorkId(String workId) {
        try {
            return restTemplate.getForObject(checkrUrl + "/" + workId, ScrapedDataDomain.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check workId: " + e.getMessage());
        }
    }

    // Inner class for request body
    static class ScrapeRequest {
        private String path;

        public ScrapeRequest(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
