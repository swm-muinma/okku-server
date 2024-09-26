package kr.okku.server.dto.controller.fitting;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FittingRequestDto {
    private MultipartFile image;
    private String pickId;
    private String part;
}
