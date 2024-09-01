package kr.okku.server.dto.controller.review;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickDto {
    private String id;
    private String image;
    private String name;
    private double price;
    private String url;
}
