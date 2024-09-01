package kr.okku.server.service;

import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.CartDomain;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PickService {
    private final PickPersistenceAdapter pickPersistenceAdapter;
    private final CartPersistenceAdapter cartPersistenceAdapter;
    private final ScraperAdapter scraperAdapter;
    private final UserRepository userRepository;

    @Autowired
    public PickService(PickPersistenceAdapter pickPersistenceAdapter, CartPersistenceAdapter cartPersistenceAdapter,
                       ScraperAdapter scraperAdapter, UserRepository userRepository) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
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
        }
        if (isDeletePermenant==false && (cartId == null || cartId.isEmpty())) {
                throw new ErrorDomain(ErrorCode.INVALID_CARTID);
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

        if (isDeletePermenant) {
            long deleteNum = pickPersistenceAdapter.deleteByIdIn(pickIds);
            this.deletePickFromAllCart(pickIds);
        } else {
            var isDeleted = this.deleteFromCart(pickIds, cartId);
        }
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
            CartDomain cart = cartPersistenceAdapter.findById(cartId)
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST));
            Page<PickDomain> pickPage = pickPersistenceAdapter.findByIdIn(cart.getPickItemIds(), pageable);
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

    public Map<String, Object> movePicks(String userId, List<String> pickIds, String sourceCartId, String destinationCartId, Boolean isDeleteFromOrigin) {
        if (isDeleteFromOrigin == null) {
            throw new ErrorDomain(ErrorCode.IS_DELETE_FROM_ORIGIN_REQUIRED);
        }
        if (destinationCartId == null || destinationCartId.isEmpty()) {
            throw new ErrorDomain(ErrorCode.DESTINATION_CART_ID_REQUIRED);
        }
        if (!isDeleteFromOrigin) {
            List<String> movedPickIds = this.addToCart(pickIds, destinationCartId);
            if (movedPickIds == null || movedPickIds.isEmpty()) {
                throw new ErrorDomain(ErrorCode.ALREADY_EXIST_CART);
            }
            if (pickIds.isEmpty()) {
                throw new ErrorDomain(ErrorCode.PICK_IDS_REQUIRED);
            }
            List<PickDomain> picksInfo = pickPersistenceAdapter.findByIdIn(pickIds);
            picksInfo.forEach(pick -> {
                if (!pick.getUserId().equals(userId)) {
                    throw new ErrorDomain(ErrorCode.NOT_OWNER);
                }
            });
            return Map.of(
                    "source", Map.of("cartId", sourceCartId != null ? sourceCartId : "__all__", "pickIds", movedPickIds),
                    "destination", Map.of("cartId", destinationCartId, "pickIds", movedPickIds)
            );
        } else {
            List<String> movedPickIds = this.moveCart(pickIds, sourceCartId, destinationCartId);
            return Map.of(
                    "source", Map.of("cartId", sourceCartId != null ? sourceCartId : "__all__", "pickIds", List.of()),
                    "destination", Map.of("cartId", destinationCartId, "pickIds", movedPickIds)
            );
        }
    }

    @Transactional
    List<String> moveCart(List<String> pickIds, String sourceCartId, String destinationCartId) {
            // Add picks to the target cart
            List<String> addedPicks = this.addToCart(pickIds, destinationCartId);
            if (addedPicks == null || addedPicks.isEmpty()) {
                throw new ErrorDomain(ErrorCode.DUPLICATED_PICK);
            }

            // Remove picks from the original carts
            this.deleteFromCart(pickIds, sourceCartId);

            return addedPicks;
    }


    @Transactional
    public List<String> addToCart(List<String> pickIds, String cartId) {
        checkPickIdExist(pickIds); // Custom method to check if pickIds exist

        // Retrieve the cart from the repository
        CartDomain cart = cartPersistenceAdapter.findById(cartId).orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST));

        // Add pick IDs to the cart
        cart.getPickItemIds().addAll(pickIds);

        // Save the updated cart
        CartDomain updatedCart = cartPersistenceAdapter.save(cart);

        // Return the pick IDs if the cart was updated, otherwise return null
        return updatedCart.getPickItemIds().containsAll(pickIds) ? pickIds : null;
    }
    @Transactional
    public List<String> deleteFromCart(List<String> pickIds, String cartId) {
        // Check if pickIds exist (this is a custom method, assumed to be implemented)
        checkPickIdExist(pickIds);

        // Retrieve the cart from the repository
        CartDomain cart = cartPersistenceAdapter.findById(cartId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST));

        // Remove pick IDs from the cart
       cart.deletePicks(pickIds);

        // Save the updated cart
        CartDomain updatedCart = cartPersistenceAdapter.save(cart);

        // Return the pick IDs if any were removed, otherwise return null
        return updatedCart.getPickItemIds().containsAll(pickIds) ? pickIds : null;
    }

    @Transactional
    public List<String> deletePickFromAllCart(List<String> pickIds) {
            // Retrieve all carts containing the pick IDs
            List<CartDomain> carts = cartPersistenceAdapter.findByPickItemIdsIn(pickIds);
            // Check if any carts were found
            if (carts.isEmpty()) {
                return null; // No carts found with the given pick IDs
            }

            // Process each cart
            for (CartDomain cart : carts) {
                // Remove the pick IDs from each cart
                cart.deletePicks(pickIds);

                // Save the updated cart
                cartPersistenceAdapter.save(cart);
            }

            // Return the pick IDs if any carts were updated, otherwise return null
            return pickIds;
    }

    private void checkPickIdExist(List<String> pickIds) {
        List<PickDomain> persistencePicks = pickPersistenceAdapter.findByIdIn(pickIds);
        if(pickIds.size() != persistencePicks.size()){
            throw new ErrorDomain(ErrorCode.INVALID_PICKIDS);
        }
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
