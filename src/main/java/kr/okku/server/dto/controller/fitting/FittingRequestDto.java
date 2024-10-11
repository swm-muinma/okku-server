package kr.okku.server.dto.controller.fitting;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FittingRequestDto extends BasicRequestDto {
    private MultipartFile image;
    private boolean isNewImage;
    private String imageForUrl;
    private String pickId;
    private String part;
}
