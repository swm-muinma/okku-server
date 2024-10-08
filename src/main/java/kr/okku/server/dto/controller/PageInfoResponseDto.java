package kr.okku.server.dto.controller;

import lombok.Data;

@Data
public class PageInfoResponseDto extends BasicRequestDto{
    private int totalDataCnt;
    private int totalPages;
    private boolean isLastPage;
    private boolean isFirstPage;
    private int requestPage;
    private int requestSize;

    // Getters and Setters
}

