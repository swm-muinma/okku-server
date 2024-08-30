package kr.okku.server.domain;

import lombok.Data;

import java.util.List;

@Data
public class ScrapedDataDomain {
    private String name;
    private int price;
    private String thumbnail_image;
    private String taskId;
    private String productPk;
    private String platform;
    private List<String> taskIds;
}