package kr.okku.server.dto.controller.admin;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FiittingListResponseDto extends BasicRequestDto {
    private String userId;
    private String userName;
    private String requestUserImage;
    private String requestItemImage;
    private String requestItemUrl;
    private String responseImage;
    private String responseMessage;
}
