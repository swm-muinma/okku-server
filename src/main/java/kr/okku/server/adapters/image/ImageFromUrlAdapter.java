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

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageFromUrlAdapter {

    private final RestTemplate restTemplate;

    public ImageFromUrlAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MultipartFile imageFromUrl(String imageUrl) {
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

                // 이미지 포맷에 상관없이 BufferedImage로 변환
                ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageBytes);
                BufferedImage originalImage = ImageIO.read(imageInputStream);

                if (originalImage == null) {
                    // ImageIO.read가 null을 반환하면 gif 또는 지원하지 않는 이미지일 수 있으므로 추가 처리
                    ImageReader gifReader = ImageIO.getImageReadersByFormatName("gif").next();
                    gifReader.setInput(ImageIO.createImageInputStream(imageInputStream), true);
                    originalImage = gifReader.read(0);
                }

                // JPEG로 변환
                ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", jpegOutputStream);
                byte[] jpegBytes = jpegOutputStream.toByteArray();

                // 파일 이름 및 ContentType 설정
                String fileName = "downloaded_image.jpg"; // JPEG 파일 이름
                String contentType = "image/jpeg"; // JPEG Content Type

                // byte[]를 MultipartFile로 변환
                return new MockMultipartFile(fileName, fileName, contentType, new ByteArrayInputStream(jpegBytes));
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new ErrorDomain(ErrorCode.INVALID_ITEM_IMAGE, null);
        }
        throw new ErrorDomain(ErrorCode.INVALID_ITEM_IMAGE, null);
    }

}