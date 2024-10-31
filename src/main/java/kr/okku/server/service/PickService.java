package kr.okku.server.service;

import kr.okku.server.adapters.persistence.*;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.mapper.PickMapper;
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
    private final Utils utils;

    @Autowired
    public PickService(PickPersistenceAdapter pickPersistenceAdapter, CartPersistenceAdapter cartPersistenceAdapter,
                       ScraperAdapter scraperAdapter, UserPersistenceAdapter userPersistenceAdapter, ItemPersistenceAdapter itemPersistenceAdapter, FittingPersistenceAdapter fittingPersistenceAdapter, Utils utils
                     ) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.scraperAdapter = scraperAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.itemPersistenceAdapter = itemPersistenceAdapter;
        this.fittingPersistenceAdapter = fittingPersistenceAdapter;
        this.utils = utils;
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
    public PickDomain createPick(TraceId traceId,String userId, NewPickRequestDto requestDto) {
        String url = extractValidUrl(requestDto.getUrl());
        System.out.println(url);
        UserDomain user = userPersistenceAdapter.findById(userId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND,traceId));
        List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);

//        utils.validatePickLimit(user,picks);

        Optional<ScrapedDataDomain> scrapedCachData = itemPersistenceAdapter.findByUrl(url);

        ScrapedDataDomain scrapedData;

        if(scrapedCachData.isEmpty()) {
            Optional<ScrapedDataDomain> scrapedRawData = scraperAdapter.scrape(url);

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
                            .build();
                }
            });
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

        return savedPick;
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
        System.out.println(requestDto);
        CartDomain cart = cartPersistenceAdapter.findById(cartId).orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST,traceId));

        cart.addPicks(pickIds);

        CartDomain updatedCart = cartPersistenceAdapter.save(cart);
        System.out.println(updatedCart);

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
