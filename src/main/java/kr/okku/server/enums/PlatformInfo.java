package kr.okku.server.enums;

import kr.okku.server.domain.PlatformDomain;

public enum PlatformInfo {
    MUSINSA("musinsa", "https://www.musinsa.com", "https://is1-ssl.mzstatic.com/image/thumb/Purple221/v4/76/ad/73/76ad735c-865e-3894-e73f-30ede996b7ac/AppIcon-0-0-1x_U007emarketing-0-7-0-85-220.png/460x0w.webp"),
    ZIGZAG("zigzag", "https://www.zigzag.kr", "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/54/f1/f0/54f1f017-27ff-9538-eb0d-283704760b69/AppIcon-0-0-1x_U007epad-0-0-85-220.png/460x0w.webp"),
    ABLY("a-bly", "https://m.a-bly.com/", "https://play-lh.googleusercontent.com/QtgW1o5zt3Z3gQedik_iYGcgz4pQhe41cZ2Lisp9PT7zV46AfQmXeS1ljbY9Ss2CnzY");

    private final String platformName;
    private final String url;
    private final String image;

    PlatformInfo(String platformName, String url, String image) {
        this.platformName = platformName;
        this.url = url;
        this.image = image;
    }


    public static PlatformDomain fromPlatformName(String platformName) {
        for (PlatformInfo info : PlatformInfo.values()) {
            if (info.platformName.equals(platformName)) {
                return PlatformDomain.builder()
                        .name(info.platformName)
                        .image(info.image)
                        .url(info.url)
                        .build();
            }
        }
        throw new IllegalArgumentException("Unknown platform name: " + platformName);
    }
}
