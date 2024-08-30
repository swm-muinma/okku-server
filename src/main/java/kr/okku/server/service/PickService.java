package kr.okku.server.service;

import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.controller.PageInfoResponseDTO;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.PlatformDomain;
import kr.okku.server.enums.PlatformInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PickService {
    private final PickPersistenceAdapter pickPersistenceAdapter;
    private final CartRepository cartRepository;
    private final ScraperAdapter scraperAdapter;
    private final UserRepository userRepository;

    @Autowired
    public PickService(PickPersistenceAdapter pickPersistenceAdapter, CartRepository cartRepository,
                       ScraperAdapter scraperAdapter, UserRepository userRepository) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartRepository = cartRepository;
        this.scraperAdapter = scraperAdapter;
        this.userRepository = userRepository;
    }
    private void validateUserAndPickLimit(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND));

        if (!user.getIsPremium()) {
            List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);
            if (picks.size() > 8) {
                throw new ErrorDomain(ErrorCode.MUST_INVITE);
            }
        }
    }

    private PickDomain createPickDomain(String userId, String url, ScrapedDataDomain scrapedData) {
        String platformName = scrapedData.getPlatform();
        PlatformDomain platformInfo = PlatformInfo.fromPlatformName(platformName);

        return PickDomain.builder()
                .name(scrapedData.getName())
                .url(url)
                .userId(userId)
                .price(scrapedData.getPrice())
                .image(scrapedData.getThumbnail_image())
                .platform(platformInfo)
                .pk(scrapedData.getProductPk())
                .build();
    }

    public PickDomain createPick(String userId, String url) {
        validateUserAndPickLimit(userId);

        var scrapedData = scraperAdapter.scrape(url);

        PickDomain pick = createPickDomain(userId, url, scrapedData);
        pickPersistenceAdapter.save(pick);

        return pick;
    }

    public void deletePicks(String userId, List<String> pickIds, String cartId, boolean isDeletePermenant) {
        if (isDeletePermenant) {
            if (cartId != null && !cartId.isEmpty()) {
                throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
            }
        } else {
            if (cartId == null || cartId.isEmpty()) {
                throw new ErrorDomain(ErrorCode.INVALID_CARTID);
            }
        }

        if (pickIds.isEmpty()) {
            throw new ErrorDomain(ErrorCode.INVALID_PICKIDS);
        }

        List<PickDomain> picksInfo = pickPersistenceAdapter.findByIdIn(pickIds);
        picksInfo.forEach(el -> {
            if (!el.getUserId().equals(userId)) {
                throw new ErrorDomain(ErrorCode.NOT_OWNER);
            }
        });

//        if (isDeletePermenant) {
//            long deleteNum = pickPersistenceAdapter.deleteByIdIn(pickIds);
//            boolean isDeletedFromCart = cartRepository.deletePickFromAllCart(pickIds);
//        } else {
//            var isDeleted = cartRepository.deleteFromCart(pickIds, cartId);
//        }
    }

    public UserPicksResponseDTO getMyPicks(String userId, String cartId, int page, int size) {
        if (page < 1) {
            throw new ErrorDomain(ErrorCode.INVALID_PAGE);
        }

        if (size < 1) {
            throw new ErrorDomain(ErrorCode.INVALID_PAGE);
        }

        PageRequest pageable = PageRequest.of(page - 1, size);

        PickCartResponseDTO cartDTO;
        List<PickItemResponseDTO> picks;
        PageInfoResponseDTO pageInfo;

        if (cartId != null) {
            CartEntity cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST));
            Page<PickDomain> pickPage = pickPersistenceAdapter.findByIdIn(Arrays.stream(cart.getPickItemIds()).toList(), pageable);
            UserEntity user = userRepository.findById(cart.getUserId())
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND));

            cartDTO = new PickCartResponseDTO();
            cartDTO.setName(cart.getName());
            cartDTO.setHost(new PickCartHostResponseDTO(cart.getUserId(), user.getName()));

            picks = pickPage.getContent().stream()
                    .map(this::convertToPickDTO)
                    .collect(Collectors.toList());

            pageInfo = convertToPageInfoDTO(pickPage);
        } else {
            Page<PickDomain> pickPage = pickPersistenceAdapter.findByUserId(userId, pageable);
            System.out.println(pickPage);
            cartDTO = new PickCartResponseDTO();
            cartDTO.setName("__all__");
            cartDTO.setHost(new PickCartHostResponseDTO(userId, "testUser"));

            picks = pickPage.getContent().stream()
                    .map(this::convertToPickDTO)
                    .collect(Collectors.toList());

            pageInfo = convertToPageInfoDTO(pickPage);
        }

        UserPicksResponseDTO responseDTO = new UserPicksResponseDTO();
        responseDTO.setCart(cartDTO);
        responseDTO.setPicks(picks);
        responseDTO.setPage(pageInfo);
        return responseDTO;
    }

    // DTO 변환 메서드들
    private PickItemResponseDTO convertToPickDTO(PickDomain pickDomain) {
        PickItemResponseDTO dto = new PickItemResponseDTO();
        dto.setId(pickDomain.getId());
        dto.setName(pickDomain.getName());
        dto.setPrice(pickDomain.getPrice());
        dto.setImage(pickDomain.getImage());
        dto.setUrl(pickDomain.getUrl());

        PickPlatformResponseDTO platformDTO = new PickPlatformResponseDTO();
        platformDTO.setName(pickDomain.getPlatform().getName());
        platformDTO.setImage(pickDomain.getPlatform().getImage());
        platformDTO.setUrl(pickDomain.getPlatform().getUrl());

        dto.setPlatform(platformDTO);
        return dto;
    }

    private PageInfoResponseDTO convertToPageInfoDTO(Page<PickDomain> page) {
        PageInfoResponseDTO pageInfo = new PageInfoResponseDTO();
        pageInfo.setTotalDataCnt((int) page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setLastPage(page.isLast());
        pageInfo.setFirstPage(page.isFirst());
        pageInfo.setRequestPage(page.getNumber() + 1);
        pageInfo.setRequestSize(page.getSize());
        return pageInfo;
    }
}
