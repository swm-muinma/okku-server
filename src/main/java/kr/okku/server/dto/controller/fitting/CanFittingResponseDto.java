package kr.okku.server.dto.controller.fitting;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CanFittingResponseDto extends BasicRequestDto {
    private String userImageUrl;
    private Boolean status;
    private String message;
}
