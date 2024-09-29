package kr.okku.server.adapters.image;

import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class ImageFromUrlAdapter {

    private final RestTemplate restTemplate;

    public ImageFromUrlAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MultipartFile imageFromUrl(String imageUrl)  {
        try {
            // 이미지 데이터를 byte[]로 받음
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    imageUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                byte[] imageBytes = response.getBody();

                // 파일 이름 및 ContentType은 응답 헤더에서 가져오거나 임의로 설정 가능
                String fileName = "downloaded_image.jpg"; // 필요에 따라 설정
                String contentType = response.getHeaders().getContentType() != null
                        ? response.getHeaders().getContentType().toString()
                        : "image/jpeg"; // 기본적으로 jpeg로 설정

                // byte[]를 MultipartFile로 변환
                return new MockMultipartFile(fileName, fileName, contentType, new ByteArrayInputStream(imageBytes));
            }
        } catch (Exception e){
            System.out.println(e);
            throw new ErrorDomain(ErrorCode.IMAGE_CONVERTER_ERROR);
        }
        throw new ErrorDomain(ErrorCode.IMAGE_CONVERTER_ERROR);
    }
}
