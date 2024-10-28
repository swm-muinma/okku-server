package kr.okku.server.dto.controller.fitting;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DeleteCachingUserImageRequestDto extends BasicRequestDto {
    private String imageUrl;
}
