    package kr.okku.server.service;

    import kr.okku.server.adapters.image.ImageFromUrlAdapter;
    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
    import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.PickDomain;
    import kr.okku.server.dto.adapter.FittingResponseDto;
    import kr.okku.server.exception.ErrorCode;
    import kr.okku.server.exception.ErrorDomain;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.*;

    @Service
    public class FittingService {

        private final ImageFromUrlAdapter imageFromUrlAdapter;
        private final PickPersistenceAdapter pickPersistenceAdapter;
        private final ScraperAdapter scraperAdapter;

        @Autowired
        public FittingService(ScraperAdapter scraperAdapter,
                              ImageFromUrlAdapter imageFromUrlAdapter,
                              PickPersistenceAdapter pickPersistenceAdapter,  ScraperAdapter scraperAdapter1) {
            this.imageFromUrlAdapter = imageFromUrlAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
            this.scraperAdapter = scraperAdapter1;
        }

        public boolean fitting(String userId, MultipartFile userImage, String pickId, String part) {
            PickDomain pick = pickPersistenceAdapter.findById(pickId).orElse(null);
            if(pick==null){
                throw new ErrorDomain(ErrorCode.PICK_NOT_EXIST);
            }
            String itemImageUrl = pick.getImage();
            byte[] itemImage = imageFromUrlAdapter.imageFromUrl(itemImageUrl);
            part = part!=null ? part : "upper_body";
            FittingResponseDto fittingResponse = scraperAdapter.fitting(userId,part,itemImage,convertMultipartFileToBytes(userImage));
            String fittingImageUrl = "https://vton-result.s3.ap-northeast-2.amazonaws.com/"+fittingResponse.getFile_key();
            pick.setFittingImage(fittingImageUrl);
            pickPersistenceAdapter.save(pick);

            return true;
        }

        private byte[] convertMultipartFileToBytes(MultipartFile file) {
            try{
                return file.getBytes();
            }catch (Exception e){
                throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
            }
        }
    }
