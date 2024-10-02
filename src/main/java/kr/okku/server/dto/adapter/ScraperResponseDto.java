package kr.okku.server.dto.adapter;

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
    private String category;
    private String brand;
}
