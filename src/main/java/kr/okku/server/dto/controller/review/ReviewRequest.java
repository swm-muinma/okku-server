package kr.okku.server.dto.controller.review;

import kr.okku.server.dto.controller.BasicRequestDto;

public class ReviewRequest extends BasicRequestDto {
    private String productPk;
    private String platform;
    private String okkuId;

    // Getters and Setters
    public String getProductPk() {
        return productPk;
    }

    public void setProductPk(String productPk) {
        this.productPk = productPk;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOkkuId() {
        return okkuId;
    }

    public void setOkkuId(String okkuId) {
        this.okkuId = okkuId;
    }
}
