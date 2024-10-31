package kr.okku.server.dto.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ScraperResponseDto {
    private String platform;
    private String product_key;
    private String url;
    private String name;
    private Integer price;
    private String img_url;
    private String brand;
    private String category;
    private String fitting_part;
}
