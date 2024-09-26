package kr.okku.server.adapters.image;

import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class ImageFromUrlAdapter {

    private final RestTemplate restTemplate;

    public ImageFromUrlAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] imageFromUrl(String imageUrl)  {
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    imageUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        }catch (Exception e){
            throw new ErrorDomain(ErrorCode.SCRAPER_ERROR);
        }
        throw new ErrorDomain(ErrorCode.SCRAPER_ERROR);
    }
}
