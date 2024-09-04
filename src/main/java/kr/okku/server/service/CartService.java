package kr.okku.server.service;

import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.dto.controller.cart.CartDto;
import kr.okku.server.dto.controller.cart.MyCartsResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartPersistenceAdapter cartPersistenceAdapter;

    private final PickPersistenceAdapter pickPersistenceAdapter;

    @Autowired
    public CartService(CartPersistenceAdapter cartPersistenceAdapter, PickPersistenceAdapter pickPersistenceAdapter) {
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.pickPersistenceAdapter = pickPersistenceAdapter;

    }

    // Method to fetch carts associated with a user with pagination
    public MyCartsResponseDto getMyCarts(String userId) {
        // Fetch carts associated with the user
        List<CartDomain> cartDomains = cartPersistenceAdapter.findByUserId(userId);

        if (cartDomains == null) {
            throw new ErrorDomain(ErrorCode.CART_NOT_EXIST);
        }

        // Map cart data and pick information
        var resCart = cartDomains.stream().map(cart -> {
            // Fetch pick information for each cart
            List<String> pickIds = cart.getPickItemIds();
            var picks = pickPersistenceAdapter.findByIdIn(pickIds);

            // Collect up to 3 pick images
            List<String> pickImages = picks.stream()
                    .map(pick -> pick.getImage())
                    .limit(3)
                    .collect(Collectors.toList());

            // Create response object for each cart
            return new CartDto(
                    cart.getId(),
                    cart.getName(),
                    pickIds.size(),
                    pickImages
            );
        }).collect(Collectors.toList());

        MyCartsResponseDto responseDto =  new MyCartsResponseDto();
        responseDto.setCarts(resCart);
        return responseDto;
    }

    // Method to delete a cart owned by the user
    @Transactional
    public String deleteCart(String userId, String cartId) {
        // Fetch cart details
        CartDomain cartInfo = cartPersistenceAdapter.findById(cartId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST));

        // Check if the user is the owner of the cart
        if (!cartInfo.getUserId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.NOT_OWNER);
        }

        // Delete the cart
        cartPersistenceAdapter.deleteById(cartId);

        return cartId;
    }

    // Method to create a new cart
    @Transactional
    public CartDomain createCart(String userId, String name, List<String> pickIds) {
        // Validate cart name
        if (name == null || name.isEmpty()) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
        }
        Integer size = 0;
        if(pickIds==null){
            pickIds = new ArrayList<>();
        }
        // Create the cart
        CartDomain cart =  CartDomain.builder()
                .pickItemIds(pickIds)
                .userId(userId)
                .name(name)
                .pickNum(size)
                .build();

        CartDomain savedCart = cartPersistenceAdapter.save(cart);
        if (savedCart != null) {
            return savedCart;
        }
        throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
    }
}
