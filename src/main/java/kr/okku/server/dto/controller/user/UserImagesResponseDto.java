package kr.okku.server.dto.controller.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class UserImagesResponseDto extends BasicRequestDto {

    private List<String> images;
}
