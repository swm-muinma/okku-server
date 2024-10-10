    package kr.okku.server.service;

    import kr.okku.server.adapters.image.ImageFromUrlAdapter;
    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.PickDomain;
    import kr.okku.server.domain.UserDomain;
    import kr.okku.server.dto.adapter.FittingResponseDto;
    import kr.okku.server.dto.controller.fitting.FittingRequestDto;
    import kr.okku.server.exception.ErrorCode;
    import kr.okku.server.exception.ErrorDomain;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    @Service
    public class FittingService {

        private final ImageFromUrlAdapter imageFromUrlAdapter;
        private final PickPersistenceAdapter pickPersistenceAdapter;

        private final UserPersistenceAdapter userPersistenceAdapter;
        private final ScraperAdapter scraperAdapter;

        @Autowired
        public FittingService(ScraperAdapter scraperAdapter,
                              ImageFromUrlAdapter imageFromUrlAdapter,
                              PickPersistenceAdapter pickPersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter) {
            this.imageFromUrlAdapter = imageFromUrlAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
            this.scraperAdapter = scraperAdapter;
            this.userPersistenceAdapter = userPersistenceAdapter;
        }

        public boolean fitting(String userId, FittingRequestDto requestDto) {
            MultipartFile userImage = requestDto.getImage();
            String pickId = requestDto.getPickId();
            String part = requestDto.getPart();
            UserDomain user = userPersistenceAdapter.findById(userId).orElse(null);

            if(user==null){
                throw new ErrorDomain(ErrorCode.USER_NOT_FOUND,requestDto);
            }

            String fcmToken = user.getFcmTokensForList()[0];

            PickDomain pick = pickPersistenceAdapter.findById(pickId).orElse(null);
            if(pick==null){
                throw new ErrorDomain(ErrorCode.PICK_NOT_EXIST,requestDto);
            }

            String itemImageUrl = pick.getImage();
            MultipartFile itemImage = imageFromUrlAdapter.imageFromUrl(itemImageUrl);
            part = part!=null ? part : "upper_body";
            FittingResponseDto fittingResponse = scraperAdapter.fitting(userId,part,itemImage,userImage,fcmToken);
            System.out.println("getFittingResponse");
            System.out.println(fittingResponse);
            String fittingImageUrl = "https://vton-result.s3.ap-northeast-2.amazonaws.com/"+fittingResponse.getFile_key();
            pick.addFittingImage(fittingImageUrl);
            pickPersistenceAdapter.save(pick);

            return true;
        }

    }
