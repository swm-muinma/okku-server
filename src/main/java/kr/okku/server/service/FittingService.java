    package kr.okku.server.service;

    import kr.okku.server.adapters.image.ImageFromUrlAdapter;
    import kr.okku.server.adapters.objectStorage.S3Client;
    import kr.okku.server.adapters.persistence.FittingPersistenceAdapter;
    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.FittingDomain;
    import kr.okku.server.domain.PickDomain;
    import kr.okku.server.domain.UserDomain;
    import kr.okku.server.dto.adapter.FittingResponseDto;
    import kr.okku.server.dto.controller.fitting.FittingRequestDto;
    import kr.okku.server.dto.controller.fitting.FittingResultDto;
    import kr.okku.server.dto.controller.fitting.GetFittingListResponseDto;
    import kr.okku.server.dto.controller.pick.FittingInfo;
    import kr.okku.server.exception.ErrorCode;
    import kr.okku.server.exception.ErrorDomain;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mock.web.MockMultipartFile;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.UnsupportedEncodingException;
    import java.net.URLEncoder;
    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class FittingService {

        private final ImageFromUrlAdapter imageFromUrlAdapter;
        private final PickPersistenceAdapter pickPersistenceAdapter;
        private final UserPersistenceAdapter userPersistenceAdapter;
        private final ScraperAdapter scraperAdapter;

        private final FittingPersistenceAdapter fittingPersistenceAdapter;

        private final S3Client s3Client;

        @Autowired
        public FittingService(ScraperAdapter scraperAdapter,
                              ImageFromUrlAdapter imageFromUrlAdapter,
                              PickPersistenceAdapter pickPersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter, FittingPersistenceAdapter fittingPersistenceAdapter, S3Client s3Client) {
            this.imageFromUrlAdapter = imageFromUrlAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
            this.scraperAdapter = scraperAdapter;
            this.userPersistenceAdapter = userPersistenceAdapter;
            this.fittingPersistenceAdapter = fittingPersistenceAdapter;
            this.s3Client = s3Client;
        }

        public GetFittingListResponseDto getFittingList(String userId){
            List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);
            List<FittingResultDto> fittingInfos = new ArrayList<>();

            picks.forEach(pick -> {
                List<FittingDomain> fittingDomains = fittingPersistenceAdapter.findByIdIn(pick.getFittingList());
                fittingDomains.forEach(fitting -> {
                    FittingResultDto info = new FittingResultDto();
                    info.setFittingImage(fitting.getImgUrl());
                    info.setStatus(fitting.getStatus());
                    info.setItemName(pick.getName());
                    info.setPickId(pick.getId());
                    info.setItemImage(pick.getImage());
                    info.setItemPlatform(pick.getPlatform().getName());
                    fittingInfos.add(info);
                });
            });

            return new GetFittingListResponseDto(fittingInfos);
        }

        public FittingResultDto getNowOne(String userId){
            List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);
            List<FittingResultDto> fittingInfos = new ArrayList<>();

            picks.forEach(pick -> {
                List<FittingDomain> fittingDomains = fittingPersistenceAdapter.findByIdIn(pick.getFittingList());
                fittingDomains.forEach(fitting -> {
                    FittingResultDto info = new FittingResultDto();
                    info.setFittingImage(fitting.getImgUrl());
                    info.setStatus(fitting.getStatus());
                    info.setItemName(pick.getName());
                    info.setPickId(pick.getId());
                    info.setItemImage(pick.getImage());
                    info.setItemPlatform(pick.getPlatform().getName());
                    fittingInfos.add(info);
                });
            });

            if(fittingInfos.size()<1){
                return null;
            }

            Collections.reverse(fittingInfos);

            Optional<FittingResultDto> result = fittingInfos.stream()
                    .filter(el -> !"done".equals(el.getStatus()))
                    .findFirst();

            return result.orElse(null);
        }

        public boolean fitting(String userId, FittingRequestDto requestDto) {
            String userImage = "";
            UserDomain user = userPersistenceAdapter.findById(userId).orElse(null);

            if(user==null){
                throw new ErrorDomain(ErrorCode.USER_NOT_FOUND,requestDto);
            }

            if (!requestDto.getIsNewImage().equals("false")) {
                userImage = s3Client.upload(requestDto.getImage());
                user.addUserImage(userImage);
                userPersistenceAdapter.save(user);
                System.out.println(userImage);
            }
            if(requestDto.getIsNewImage().equals("false")){
                userImage = requestDto.getImageForUrl();
            }

            String pickId = requestDto.getPickId();
            String part = requestDto.getPart();

            String fcmToken = user.getFcmTokensForList()[0];
            PickDomain pick = pickPersistenceAdapter.findById(pickId).orElse(null);
            if(pick==null){
                throw new ErrorDomain(ErrorCode.PICK_NOT_EXIST,requestDto);
            }

            String itemImageUrl = pick.getImage();
            MultipartFile itemImage = imageFromUrlAdapter.imageFromUrl(itemImageUrl);
            part = part!=null ? part : pick.getFittingPart();
            if(part==null || part == "" || part.length()==0 ){
                part="upper_body";
            }

            String clothesPk = pick.getPk();
            if(clothesPk==null || clothesPk==""){
                clothesPk=pick.getId();
            }
            System.out.printf("fcm token : %s\n",fcmToken);
            FittingResponseDto fittingResponse = scraperAdapter.fitting(userId,part,itemImage,userImage,fcmToken,clothesPk,pick.getPlatform().getName());
            pick.addFittingList(fittingResponse.getId());
            pickPersistenceAdapter.save(pick);

            return true;
        }

    }
