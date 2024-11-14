package kr.okku.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.adapters.persistence.*;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.dto.service.CreatePickDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.mapper.PickMapper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Service
public class PickService {
    private final PickPersistenceAdapter pickPersistenceAdapter;
    private final CartPersistenceAdapter cartPersistenceAdapter;
    private final ScraperAdapter scraperAdapter;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final ItemPersistenceAdapter itemPersistenceAdapter;
    private final FittingPersistenceAdapter fittingPersistenceAdapter;
    private final ReviewPersistenceAdapter reviewPersistenceAdapter;

    private final ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter;
    private final Utils utils;

    @Autowired
    public PickService(PickPersistenceAdapter pickPersistenceAdapter, CartPersistenceAdapter cartPersistenceAdapter,
                       ScraperAdapter scraperAdapter, UserPersistenceAdapter userPersistenceAdapter, ItemPersistenceAdapter itemPersistenceAdapter, FittingPersistenceAdapter fittingPersistenceAdapter, ReviewPersistenceAdapter reviewPersistenceAdapter, ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter, Utils utils
                     ) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.scraperAdapter = scraperAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.itemPersistenceAdapter = itemPersistenceAdapter;
        this.fittingPersistenceAdapter = fittingPersistenceAdapter;
        this.reviewPersistenceAdapter = reviewPersistenceAdapter;
        this.reviewInsightPersistenceAdapter = reviewInsightPersistenceAdapter;
        this.utils = utils;
    }

    public List<ReviewDetailDomain> getReviewDetailDomainsFrom29cm(String jsonData) {
        // JSON 파서 설정
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonData);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.SCRAPER_ERROR, new TraceId());
        }

        // 리뷰 데이터를 포함하는 노드 탐색
        JsonNode dataNode = rootNode.path("data");
        JsonNode resultsNode = dataNode.path("results");

        // 전체 리뷰 개수 출력
        int totalReviewCount = dataNode.path("count").asInt();
        List<ReviewDetailDomain> reviewDetailDomains = new ArrayList<>();

        // 개별 리뷰 정보 출력
        for (JsonNode reviewNode : resultsNode) {
            String userId = reviewNode.path("userId").asText();
            int point = reviewNode.path("point").asInt();
            String contents = reviewNode.path("contents").asText();

            // uploadFiles 배열의 이미지 URL 가져오기 (null 체크 포함)
            JsonNode uploadFilesNode = reviewNode.path("uploadFiles");
            List<String> imageUrls = new ArrayList<>();

            if (uploadFilesNode.isArray()) {
                for (JsonNode fileNode : uploadFilesNode) {
                    String url = fileNode.path("url").asText();  // 각 파일의 URL을 가져옴
                    if (!url.isEmpty()) {
                        imageUrls.add("https://img.29cm.co.kr"+url);  // 리스트에 추가
                    }
                }
            }

            ReviewDetailDomain reviewDetailDomain = ReviewDetailDomain.builder()
                    .content(contents)
                    .rating(point)
                    .imageUrl(imageUrls)  // 이미지 URL 리스트를 전달
                    .build();

            reviewDetailDomains.add(reviewDetailDomain);
        }

        return reviewDetailDomains;
    }

    public List<ReviewDetailDomain> getReviewDetailDomainsFromWconcept(String html){
        try {
            Document doc = Jsoup.parse(html);

            List<ReviewDetailDomain> reviewDetailDomains = new ArrayList<>();

            Elements reviewRows = doc.select("tr");

            for (Element reviewRow : reviewRows) {
                // Extract rating stars
                Elements starRating = reviewRow.select(".star-grade > strong");
                Integer rating = this.convertWidthToRating(starRating.attr("style"));  // rating might be based on the width percentage

                // Extract product options
                Elements productOptions = reviewRow.select(".pdt_review_option p span");
//            String purchaseOption = productOptions.get(0).text();  // Example: "구매옵션 : Black_L"
//            String sizeInfo = productOptions.get(1).text(); // Example: "사이즈 정보 : 172cm,59kg,M/66,29inch"

                // Extract review date and reviewer info
                Elements reviewerInfo = reviewRow.select(".product_review_info_right > em");
//            String reviewer = reviewerInfo.text();  // Reviewer: sm********
//            String reviewDate = reviewRow.select(".product_review_info_right > span").text();  // Date: 2024.11.06

                // Extract review text
                String reviewText = reviewRow.select(".pdt_review_text").text();  // Review content

                // Extract images associated with the review
                Elements images = reviewRow.select(".pdt_review_photo img");
                List<String> imageUrls = new ArrayList<>();
                for (Element image : images) {
                    String imageUrl = image.attr("src");
                    imageUrls.add(imageUrl);
                }

                // Print extracted review details
//                System.out.println("Rating: " + rating);
//            System.out.println("Purchase Option: " + purchaseOption);
//            System.out.println("Size Info: " + sizeInfo);
//            System.out.println("Reviewer: " + reviewer);
//            System.out.println("Review Date: " + reviewDate);
//                System.out.println("Review Text: " + reviewText);
//                System.out.println("---");
                ReviewDetailDomain reviewDetailDomain = ReviewDetailDomain.builder()
                        .rating(Integer.valueOf(rating))
                        .content(reviewText)
                        .imageUrl(imageUrls)
                        .build();
                reviewDetailDomains.add(reviewDetailDomain);
            }
            return reviewDetailDomains;
        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    private static int convertWidthToRating(String widthString) {
        // "width: 100%"에서 숫자 부분만 추출
        int percent = Integer.parseInt(widthString.replaceAll("[^0-9]", ""));

        // 별점 계산: 5점 만점에서 비례하는 별점 계산
        int rating = (percent * 5) / 100;

        // 0점이 나오는 경우는 1점으로 설정 (1~5점 사이로 반환)
        return Math.max(rating, 1);
    }

    public List<ReviewDetailDomain> getReviewDetailDomainsFromMusinsa(String jsonData) {

        // JSON parser setup
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(jsonData);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.SCRAPER_ERROR, new TraceId());
        }

        // Access the list of reviews
        JsonNode dataNode = rootNode.path("data");
        JsonNode listNode = dataNode.path("list");

        List<ReviewDetailDomain> reviewDetailDomains = new ArrayList<>();

        // Loop through each review in the list
        for (JsonNode reviewNode : listNode) {
            // Extract user nickname
            String userNickName = reviewNode.path("userProfileInfo").path("userNickName").asText();

            // Extract grade (convert from String to int)
            String gradeStr = reviewNode.path("grade").asText();
            int grade = gradeStr.isEmpty() ? 0 : Integer.parseInt(gradeStr);

            // Extract content
            String content = reviewNode.path("content").asText();

            // Extract all image URLs from the "images" array
            List<String> imageUrls = new ArrayList<>();
            JsonNode imagesNode = reviewNode.path("images");
            if (imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    String imageUrl = imageNode.path("imageUrl").asText();
                    if (!imageUrl.isEmpty()) {
                        imageUrls.add("https://image.msscdn.net/thumbnails"+imageUrl);
                    }
                }
            }

            // Create a ReviewDetailDomain object and add to the list
            ReviewDetailDomain reviewDetailDomain = ReviewDetailDomain.builder()
                    .content(content)
                    .rating(grade)
                    .imageUrl(imageUrls)
                    .build();
            reviewDetailDomains.add(reviewDetailDomain);
        }

        return reviewDetailDomains;
    }

    public Boolean isLastPageFromZigzag(String jsonData, Integer page){
//        System.out.println(jsonData);
        // JSON parser setup
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonData);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.SCRAPER_ERROR, new TraceId());
        }

        JsonNode dataNode = rootNode.path("data");
        JsonNode feedListNode = dataNode.path("feed_list");
        Integer totalCount = feedListNode.path("total_count").asInt();
        if((totalCount/100)<=page){
            return true;
        }
        if(page>5) {
            return true;
        }

        return false;
    }

    public Boolean isLastPageFromWconcept(String html, Integer page){
        if(page>83){
            return true;
        }

        Document doc = Jsoup.parse(html);

        // "no_data" 클래스가 있는 요소를 찾음
        Element noDataElement = doc.selectFirst(".no_data");

        // 요소가 존재하면 true, 없으면 false 반환
        return noDataElement != null;
    }

    public List<ReviewDetailDomain> getReviewDetailDomainsFromZigzag(String jsonData) {

        // JSON parser setup
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonData);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.SCRAPER_ERROR, new TraceId());
        }

        JsonNode dataNode = rootNode.path("data");
        JsonNode feedListNode = dataNode.path("feed_list");
        JsonNode itemListNode = feedListNode.path("item_list");
        int totalCount = feedListNode.path("total_count").asInt();

        List<ReviewDetailDomain> reviewDetailDomains = new ArrayList<>();

        for (JsonNode reviewNode : itemListNode) {
            String contents = reviewNode.path("contents").asText();
            int point = reviewNode.path("rating").asInt();

            JsonNode attachmentListNode = reviewNode.path("attachment_list");
            List<String> imageUrls = new ArrayList<>();
            if (attachmentListNode.isArray()) {
                for (JsonNode fileNode : attachmentListNode) {
                    String originalUrl = fileNode.path("original_url").asText();
                    imageUrls.add(originalUrl);
                }
            }

            // Create ReviewDetailDomain and add to the list
            ReviewDetailDomain reviewDetailDomain = ReviewDetailDomain.builder()
                    .content(contents)
                    .rating(point)
                    .imageUrl(imageUrls)
                    .build();
            reviewDetailDomains.add(reviewDetailDomain);
        }

        return reviewDetailDomains;
    }

    public static String extractValidUrl(String input) {
        String urlPattern = "(https?://\\S+)(\\s|$)";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public SubmitRawReviewsResponseDto submitRawReviews(TraceId traceId, SubmitRawReviewsRequestDto request){
        String pk = request.getPk();
        String platform = request.getPlatform();

        List<ReviewDetailDomain> reviewDetailDomains = new ArrayList<>();
//        System.out.println(request.getData().get(0));
        if(platform.equals("29cm")){
            reviewDetailDomains = this.getReviewDetailDomainsFrom29cm(request.getData().get(0));
        }
        if(platform.equals("musinsa")){
            reviewDetailDomains = this.getReviewDetailDomainsFromMusinsa(request.getData().get(0));
        }
        if(platform.equals("zigzag")){
            for(String data : request.getData()){
                List<ReviewDetailDomain> reviewsFromCurrentData = this.getReviewDetailDomainsFromZigzag(data);

                reviewDetailDomains.addAll(reviewsFromCurrentData);
            }
        }
        if(platform.equals("wconcept")){
            for(String data : request.getData()){
                List<ReviewDetailDomain> reviewsFromCurrentData = this.getReviewDetailDomainsFromWconcept(data);
                reviewDetailDomains.addAll(reviewsFromCurrentData);
            }
        }

        ReviewDomain reviewDomain = ReviewDomain.builder()
                .reviews(reviewDetailDomains)
                .productKey(pk)
                .isDoneScrapeReviews(true)
                .platform(platform)
                .crawlerVersion("2.0.0")
                .build();

        ReviewDomain savedReviewDomain = reviewPersistenceAdapter.save(reviewDomain);

        String status="review_empty";

        if(!savedReviewDomain.getReviews().isEmpty()){
            status = scraperAdapter.crateInsight(traceId.getId(),pk,platform);
        }



        SubmitRawReviewsResponseDto response = SubmitRawReviewsResponseDto.builder()
                .pk(pk)
                .platform(platform)
                .status(status)
                .traceId(traceId.getId())
                .build();

        return response;
    }

    public GetNextPageForRawReviewsResponseDto getNextPageForRawReviews(TraceId traceId, GetNextPageForRawReviewsRequestDto request){
        String platform = request.getPlatform();
        String pk = request.getPk();
        Integer page = request.getPage();
        String url = "";
        RequestBodyDto requestBody = RequestBodyDto.builder().build();

        Boolean lastPage = true;
        if(request.getPlatform().equals("zigzag")){
            requestBody = RequestBodyDto.builder()
                    .method("post")
                    .type("application/json")
                    .data(this.createZigzagGraphQLRequest(pk,page))
                    .build();
            url="https://api.zigzag.kr/api/2/graphql/batch/GetNormalReviewFeedList";
            lastPage=this.isLastPageFromZigzag(request.getData(),page);
        }
        if(request.getPlatform().equals("29cm")){

        }
        if(request.getPlatform().equals("musinsa")){

        }
        if(request.getPlatform().equals("wconcept")){
            requestBody = RequestBodyDto.builder()
                    .method("post")
                    .type("multipart/form-data")
                    .data(this.createWconceptFormdataRequest(pk,page))
                    .build();
            url="https://www.wconcept.co.kr/Ajax/ProductReViewList";
            lastPage=this.isLastPageFromWconcept(request.getData(),page);
        }

        GetNextPageForRawReviewsResponseDto response = GetNextPageForRawReviewsResponseDto.builder()
                .urlForRawReviews(url)
                .lastPage(lastPage)
                .requestBody(requestBody)
                .page(page)
                .traceId(request.getTraceId())
                .platform(platform)
                .pk(pk)
                .build();

        return response;
    }

    public CreatePickResponseDto createPickForRawReviews(TraceId traceId,String userId, NewPickRequestDto requestDto) {
        CreatePickDto createPickDto = this.createPick(traceId, userId,requestDto);
        PickDomain savedPick = createPickDto.getPickDomain();
        String traceIdForAi = createPickDto.getTraceId();
        String platform = savedPick.getPlatform().getName();
        String pk = savedPick.getPk();

        CreatePickResponseDto tempResponse = CreatePickResponseDto.builder()
                .id(savedPick.getId())
                .pk(savedPick.getPk())
                .brand(savedPick.getBrand())
                .category(savedPick.getCategory())
                .fittingList(savedPick.getFittingList())
                .fittingPart(savedPick.getFittingPart())
                .price(savedPick.getPrice())
                .image(savedPick.getImage())
                .userId(savedPick.getUserId())
                .url(savedPick.getUrl())
                .platform(savedPick.getPlatform())
                .name(savedPick.getName())
                .build();

        Optional<ReviewDomain> reviewDomain = reviewPersistenceAdapter.findByProductPkAndPlatform(pk,platform);

        Boolean isLast = false;
        // caching : prod에서는 주석 제거하기
//        if(!reviewDomain.isEmpty()){
//            isLast=true;
//        }

        if(savedPick.getPlatform().getName().equals("zigzag")){
            return createZigzagRequestBody(tempResponse,savedPick.getPk(),traceIdForAi,1,isLast);
        }

        if(savedPick.getPlatform().getName().equals("musinsa")){
            return createMusinsaRequestBody(tempResponse,savedPick.getPk(),traceIdForAi,0,isLast);
        }

        if(savedPick.getPlatform().getName().equals("29cm")){
            return create29cmRequestBody(tempResponse,savedPick.getPk(),traceIdForAi,0,isLast);
        }

        if(savedPick.getPlatform().getName().equals("wconcept")){
            return createWconceptRequestBody(tempResponse,savedPick.getPk(),traceIdForAi,1,isLast);
        }

        return tempResponse;
    }

    private CreatePickResponseDto createZigzagRequestBody(CreatePickResponseDto response, String pk, String traceId, Integer page,Boolean isLast){
        response.setUrlForRawReviews("https://api.zigzag.kr/api/2/graphql/batch/GetNormalReviewFeedList");
        response.setLastPage(isLast);
        RequestBodyDto requestBody = RequestBodyDto.builder()
                .method("post")
                .type("application/json")
                .data(this.createZigzagGraphQLRequest(pk,page))
                .build();
        response.setRequestBody(requestBody);
        response.setPage(page);
        response.setTraceId(traceId);
        return response;
    }

    private CreatePickResponseDto createMusinsaRequestBody(CreatePickResponseDto response, String pk, String traceId, Integer page, Boolean isLast){
        response.setUrlForRawReviews("https://goods.musinsa.com/api2/review/v1/view/list?page=0&pageSize=500&goodsNo="+pk+"&sort=up_cnt_desc");
        response.setLastPage(isLast);
        RequestBodyDto requestBody = RequestBodyDto.builder()
                .method("get")
                .type("application/json")
                .data(null)
                .build();
        response.setRequestBody(requestBody);
        response.setPage(page);
        response.setTraceId(traceId);
        return response;
    }

    private CreatePickResponseDto create29cmRequestBody(CreatePickResponseDto response, String pk, String traceId, Integer page,Boolean isLast){
        response.setUrlForRawReviews("https://review-api.29cm.co.kr/api/v4/reviews?itemId="+pk+"&page=0&size=500");
        response.setLastPage(isLast);
        RequestBodyDto requestBody = RequestBodyDto.builder()
                .method("get")
                .type("application/json")
                .data(null)
                .build();
        response.setRequestBody(requestBody);
        response.setPage(page);
        response.setTraceId(traceId);
        return response;
    }

    private CreatePickResponseDto createWconceptRequestBody(CreatePickResponseDto response, String pk, String traceId, Integer page,Boolean isLast){
        response.setUrlForRawReviews("https://www.wconcept.co.kr/Ajax/ProductReViewList");
        response.setLastPage(isLast);
        RequestBodyDto requestBody = RequestBodyDto.builder()
                .method("post")
                .type("multipart/form-data")
                .data(this.createWconceptFormdataRequest(pk,page))
                .build();
        response.setRequestBody(requestBody);
        response.setPage(page);
        response.setTraceId(traceId);
        return response;
    }

    private Map<String, Object> createZigzagGraphQLRequest(String pk,Integer page) {
        Map<String, Object> variables = new HashMap<>();
        // 동적 요청 바디를 위한 Map 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("operationName", "GetNormalReviewFeedList");
        requestBody.put("query", "query GetNormalReviewFeedList($product_id: ID!, $limit_count: Int, $skip_count: Int, $order: ProductReviewListOrderType) { feed_list: product_review_list(product_id: $product_id, limit_count: $limit_count, skip_count: $skip_count, order: $order) { total_count item_list { id contents date_created rating attachment_list { original_url thumbnail_url } reviewer { profile { nickname } } } } }");
        requestBody.put("variables", variables);
        Integer skipCount = (page-1)*100;
        try {
            variables.put("order", "BEST_SCORE_DESC");
            variables.put("limit_count", 100);
            variables.put("product_id", pk);
            variables.put("skip_count", skipCount);

            // JSON 문자열로 변환
            return requestBody;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JSON request body", e);
        }
    }

    private Map<String, Object> createWconceptFormdataRequest(String pk, Integer page) {
        Map<String, Object> variables = new HashMap<>();
        // 동적 요청 바디를 위한 Map 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("itemcd", pk);
        requestBody.put("pageIndex", page);
        requestBody.put("order", 1);
        requestBody.put("mediumcd", "M33439436");

        try {
            return requestBody;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JSON request body", e);
        }
    }

    public CreatePickDto createPick(TraceId traceId, String userId, NewPickRequestDto requestDto) {
        String url = extractValidUrl(requestDto.getUrl());
        UserDomain user = userPersistenceAdapter.findById(userId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND,traceId));
        List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);
        String traceIdForAi= traceId.getId();
//        utils.validatePickLimit(user,picks);

        Optional<ScrapedDataDomain> scrapedCachData = itemPersistenceAdapter.findByUrl(url);

        ScrapedDataDomain scrapedData;

        if(scrapedCachData.isEmpty()) {
            Optional<ScrapedDataDomain> scrapedRawData = scraperAdapter.scrape(traceId,url);
            scrapedData = scrapedRawData.orElseGet(() -> {
                try {
                    Document document = Jsoup.connect(url).get();
                    return ScrapedDataDomain.fromDocument(document, url);
                } catch (IOException e) {
                    return ScrapedDataDomain.builder()
                            .name("제목 없음")
                            .image("default-image.png")
                            .url(url)
                            .platform("Unknown Platform")
                            .traceId(traceId.getId())
                            .build();
                }
            });
            traceIdForAi = scrapedData.getTraceId();
            String platformName = scrapedData.getPlatform();
            String productPk = scrapedData.getProductPk();
            Optional<ScrapedDataDomain> scrapedCachDataByKey = itemPersistenceAdapter.findByPlatformAndProductpk(platformName,productPk);

            if (scrapedData.getPrice() != 0) {
                if (scrapedCachDataByKey.isEmpty()) {
                    itemPersistenceAdapter.save(
                            url,
                            scrapedData.getName(),
                            scrapedData.getImage(),
                            scrapedData.getPrice(),
                            scrapedData.getProductPk(),
                            scrapedData.getPlatform(),
                            1
                    );

            } else {
                    Integer pickNum = itemPersistenceAdapter.getPickNum(platformName,productPk);
                    itemPersistenceAdapter.update(
                            scrapedData.getPlatform(),
                            scrapedData.getProductPk(),
                            pickNum+1);
            }
        }
        }else{
            scrapedData=scrapedCachData.get();
            Integer pickNum = itemPersistenceAdapter.getPickNum(scrapedData.getPlatform(),scrapedData.getProductPk());
            itemPersistenceAdapter.update(
                    scrapedData.getPlatform(),
                    scrapedData.getProductPk(),
                    pickNum+1);
        }

        PickDomain pick = PickDomain.builder()
                .setPickDomainFromScrapedData(userId, url, scrapedData)
                .build();
        PickDomain savedPick = pickPersistenceAdapter.save(pick);

        return new CreatePickDto(savedPick,traceIdForAi);
    }

    public void deletePicks(TraceId traceId,String userId, DeletePicksRequestDto requestDto) {
        List<String> pickIds= requestDto.getPickIds();
        String cartId=requestDto.getCartId();
        boolean isDeletePermenant =requestDto.isDeletePermenant();

        if (isDeletePermenant) {
            if (cartId != null && !cartId.isEmpty()) {
                throw new ErrorDomain(ErrorCode.CARTID_IS_EMPTY,traceId);
            }
        }
        if (isDeletePermenant==false && (cartId == null || cartId.isEmpty())) {
                throw new ErrorDomain(ErrorCode.IF_IS_DELETE_PERMENANT_IS_FALSE_THEN_CARTID_IS_REQUIRED,traceId);
        }

        if (pickIds.isEmpty()) {
            throw new ErrorDomain(ErrorCode.PICKID_IS_EMPTY,traceId);
        }

        List<PickDomain> picksInfo = pickPersistenceAdapter.findByIdIn(pickIds);
        picksInfo.forEach(el -> {
            if (!el.getUserId().equals(userId)) {
                throw new ErrorDomain(ErrorCode.NOT_PICK_OWNER,traceId);
            }
        });

        if (isDeletePermenant) {
            long deleteNum = pickPersistenceAdapter.deleteByIdIn(pickIds);
            this.deletePickFromAllCart(pickIds);
        } else {
            var isDeleted = this.deleteFromCart(traceId,pickIds, cartId,requestDto);
        }
    }

    public UserPicksResponseDto getMyPicks(TraceId traceId,String userId, GetMyPickRequestDto requestDto) {
        String cartId = requestDto.getCartId();
        int page = requestDto.getPage();
        int size = requestDto.getSize();

        PageRequest pageable = PageRequest.of(page, size);

        PickCartResponseDto cartDTO;
        List<PickItemResponseDto> picks;
        PageInfoResponseDto pageInfo;

        if (cartId != null) {
            CartDomain cart = cartPersistenceAdapter.findById(cartId)
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST,traceId));
            Page<PickDomain> pickPage = pickPersistenceAdapter.findByIdIn(cart.getPickItemIds(), pageable);
            UserDomain user = userPersistenceAdapter.findById(cart.getUserId())
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND,traceId));

            cartDTO = new PickCartResponseDto();
            cartDTO.setName(cart.getName());
            cartDTO.setHost(new PickCartHostResponseDto(cart.getUserId(), user.getName()));

            picks = pickPage.getContent().stream()
                    .map(PickMapper::convertToPickDTO)
                    .collect(Collectors.toList());

            pageInfo = PickMapper.convertToPageInfoDTO(pickPage);
        } else {
            Page<PickDomain> pickPage = pickPersistenceAdapter.findByUserId(userId, pageable);
            cartDTO = new PickCartResponseDto();
            cartDTO.setName("__all__");
            cartDTO.setHost(new PickCartHostResponseDto(userId, "testUser"));

            picks = pickPage.getContent().stream()
                    .map(PickMapper::convertToPickDTO)
                    .collect(Collectors.toList());

            pageInfo = PickMapper.convertToPageInfoDTO(pickPage);
        }

        UserPicksResponseDto responseDTO = new UserPicksResponseDto();
        responseDTO.setCart(cartDTO);
        responseDTO.setPicks(picks);
        responseDTO.setPage(pageInfo);
        return responseDTO;
    }

    public Map<String, Object> movePicks(TraceId traceId,String userId,MovePicksRequestDto requestDto) {
        List<String> pickIds = requestDto.getPickIds();
        String sourceCartId = requestDto.getSourceCartId();
        String destinationCartId =requestDto.getDestinationCartId();
        Boolean isDeleteFromOrigin = requestDto.isDeleteFromOrigin();
        if (isDeleteFromOrigin == null) {
            throw new ErrorDomain(ErrorCode.IS_DELETE_FROM_ORIGIN_REQUIRED,traceId);
        }
        if (destinationCartId == null || destinationCartId.isEmpty()) {
            throw new ErrorDomain(ErrorCode.DESTINATION_CART_ID_REQUIRED,traceId);
        }
        if (!isDeleteFromOrigin) {
            List<String> movedPickIds = this.addToCart(traceId,pickIds, destinationCartId, requestDto);
            if (movedPickIds == null || movedPickIds.isEmpty()) {
                throw new ErrorDomain(ErrorCode.ALREADY_EXIST_CART,traceId);
            }
            if (pickIds.isEmpty()) {
                throw new ErrorDomain(ErrorCode.PICK_IDS_REQUIRED,traceId);
            }
            List<PickDomain> picksInfo = pickPersistenceAdapter.findByIdIn(pickIds);
            picksInfo.forEach(pick -> {
                if (!pick.getUserId().equals(userId)) {
                    throw new ErrorDomain(ErrorCode.NOT_PICK_OWNER,traceId);
                }
            });
            return Map.of(
                    "source", Map.of("cartId", sourceCartId != null ? sourceCartId : "__all__", "pickIds", movedPickIds),
                    "destination", Map.of("cartId", destinationCartId, "pickIds", movedPickIds)
            );
        } else {
            List<String> movedPickIds = this.moveCart(traceId,pickIds, sourceCartId, destinationCartId,requestDto);
            return Map.of(
                    "source", Map.of("cartId", sourceCartId != null ? sourceCartId : "__all__", "pickIds", List.of()),
                    "destination", Map.of("cartId", destinationCartId, "pickIds", movedPickIds)
            );
        }
    }

    @Transactional
    List<String> moveCart(TraceId traceId,List<String> pickIds, String sourceCartId, String destinationCartId, MovePicksRequestDto requestDto) {
            List<String> addedPicks = this.addToCart(traceId,pickIds, destinationCartId,requestDto);
            if (addedPicks == null || addedPicks.isEmpty()) {
                throw new ErrorDomain(ErrorCode.DUPLICATED_PICK,traceId);
            }

            this.deleteFromCart(traceId,pickIds, sourceCartId,requestDto);

            return addedPicks;
    }

    @Transactional
    public List<String> addToCart(TraceId traceId,List<String> pickIds, String cartId, BasicRequestDto requestDto) {
        checkPickIdExist(traceId,pickIds, requestDto);
        CartDomain cart = cartPersistenceAdapter.findById(cartId).orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST,traceId));

        cart.addPicks(pickIds);

        CartDomain updatedCart = cartPersistenceAdapter.save(cart);

        return updatedCart.getPickItemIds();
    }
    @Transactional
    public List<String> deleteFromCart(TraceId traceId,List<String> pickIds, String cartId,BasicRequestDto requestDto) {
        checkPickIdExist(traceId,pickIds,requestDto);

        CartDomain cart = cartPersistenceAdapter.findById(cartId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST,traceId));

       cart.deletePicks(pickIds);

        CartDomain updatedCart = cartPersistenceAdapter.save(cart);

        return updatedCart.getPickItemIds().containsAll(pickIds) ? pickIds : null;
    }

    @Transactional
    public List<String> deletePickFromAllCart(List<String> pickIds) {
            List<CartDomain> carts = cartPersistenceAdapter.findByPickItemIdsIn(pickIds);
            if (carts.isEmpty()) {
            }

            for (CartDomain cart : carts) {
                cart.deletePicks(pickIds);

                cartPersistenceAdapter.save(cart);
            }

            return pickIds;
    }

    private void checkPickIdExist(TraceId traceId,List<String> pickIds, BasicRequestDto requestDto) {
        List<PickDomain> persistencePicks = pickPersistenceAdapter.findByIdIn(pickIds);
        if(pickIds.size() != persistencePicks.size()){
            throw new ErrorDomain(ErrorCode.INVALID_PICKIDS,traceId);
        }
    }

    public PickFittingResponseDto getFitting(TraceId traceId,String pickId){
        PickDomain pick = pickPersistenceAdapter.findById(pickId).orElse(null);
        if(pick==null){
            throw new ErrorDomain(ErrorCode.PICK_NOT_EXIST,traceId);
        }
        PickPlatformResponseDto pickPlatformResponse = new PickPlatformResponseDto();
        pickPlatformResponse.setName(pick.getPlatform().getName());
        pickPlatformResponse.setUrl(pick.getPlatform().getUrl());
        pickPlatformResponse.setImage(pick.getPlatform().getImage());

        List<FittingDomain> fittingDomains = fittingPersistenceAdapter.findByIdIn(pick.getFittingList());

        List<FittingInfo> fittingInfos = (List<FittingInfo>) fittingDomains.stream().map(el->{
            FittingInfo info = new FittingInfo();
            info.setImage(el.getImgUrl());
            info.setStatus(el.getStatus());
            return info;
        }).collect(Collectors.toList());

        return new PickFittingResponseDto(pick.getId(),pick.getName(),pick.getPrice(),pick.getImage(),pick.getUrl(),pickPlatformResponse,fittingInfos);
    }

}
