    package kr.okku.server.service;

    import kr.okku.server.adapters.image.ImageFromUrlAdapter;
    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
    import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.PickDomain;
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

        private final List<String> okkuIds = new ArrayList<>();

        @Autowired
        public FittingService(ScraperAdapter scraperAdapter,
                              ImageFromUrlAdapter imageFromUrlAdapter,
                              PickPersistenceAdapter pickPersistenceAdapter,  ScraperAdapter scraperAdapter1) {
            this.imageFromUrlAdapter = imageFromUrlAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
            this.scraperAdapter = scraperAdapter1;
        }

        public boolean fitting(String userId, MultipartFile userImage, String pickId) {
            PickDomain pick = pickPersistenceAdapter.findById(pickId).orElse(null);
            if(pick==null){
                throw new ErrorDomain(ErrorCode.PICK_NOT_EXIST);
            }
            String itemImageUrl = pick.getImage();
            byte[] itemImage = imageFromUrlAdapter.imageFromUrl(itemImageUrl);
            scraperAdapter.fitting(userId,"upper_body",itemImage,convertMultipartFileToBytes(userImage));

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
