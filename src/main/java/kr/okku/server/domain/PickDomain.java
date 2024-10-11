package kr.okku.server.domain;
import kr.okku.server.enums.PlatformInfo;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PickDomain {
    private String id;
    private String url;
    private String userId;
    private String name;
    private int price;
    private String image;

    private List<String> fittingList;
    private PlatformDomain platform;
    private String pk;
    private String brand;
    private String category;
    private String fittingPart;

    public static class PickDomainBuilder {
        // 빌더 내부에 추가적인 초기화 메서드 구현
        public PickDomainBuilder setPickDomainFromScrapedData(String userId, String url, ScrapedDataDomain scrapedData) {
            String platformName = scrapedData.getPlatform();
            PlatformDomain platformInfo = PlatformInfo.fromPlatformName(platformName);

            this.userId = userId;
            this.url = url;
            this.name = scrapedData.getName();
            this.price = scrapedData.getPrice();
            this.image = scrapedData.getImage();
            this.platform = platformInfo;
            this.pk = scrapedData.getProductPk();

            return this;
        }
    }
    public void validatePickName() {
        if (name == null || name.isEmpty()) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS,null);
        }
    }

    public void addFittingList(String image){

        List<String> modifiableFittingList = new ArrayList<>(this.fittingList);
        modifiableFittingList.add(image);
        this.fittingList = modifiableFittingList;
    }

}