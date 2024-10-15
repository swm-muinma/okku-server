package kr.okku.server.service;

import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.dto.controller.cart.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public MyCartsResponseDto getMyCarts(String userId) {
        List<CartDomain> cartDomains = cartPersistenceAdapter.findByUserId(userId);
        System.out.println(cartDomains);

        if (cartDomains == null) {
            throw new ErrorDomain(ErrorCode.CART_NOT_EXIST,null);
        }

        var resCart = cartDomains.stream().map(cart -> {
            List<String> pickIds = cart.getPickItemIds();
                var picks = pickPersistenceAdapter.findByIdIn(pickIds);

            List<String> pickImages = picks.stream()
                    .map(pick -> pick.getImage())
                    .limit(3)
                    .collect(Collectors.toList());

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



    @Transactional
    public String deleteCart(String userId, String cartId) {
        CartDomain cartInfo = cartPersistenceAdapter.findById(cartId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.CART_NOT_EXIST,null));
        if (!cartInfo.getUserId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.NOT_CART_OWNER,null);
        }
        cartPersistenceAdapter.deleteById(cartId);
        return cartId;
    }

    @Transactional
    public CartDomain createCart(String userId, CreateCartRequestDto requestDto) {
        String name = requestDto.getName();
        List<String> pickIds = requestDto.getPickIds();

        if (name == null || name.isEmpty()) {
            throw new ErrorDomain(ErrorCode.NAME_IS_EMPTY,requestDto);
        }
        Integer size = 0;
        if(pickIds==null){
            pickIds = new ArrayList<>();
        }
        CartDomain cart =  CartDomain.builder()
                .pickItemIds(pickIds)
                .userId(userId)
                .name(name)
                .pickNum(size)
                .build();
        List<CartDomain> userCarts = cartPersistenceAdapter.findByUserId(userId);
        int maxOrderIndex = userCarts.stream()
                .mapToInt(CartDomain::getOrderIndex)
                .max()
                .orElse(0); // Default to 0 if no carts exist

        cart.setOrderIndex(maxOrderIndex + 1);
        CartDomain savedCart = cartPersistenceAdapter.save(cart);
        if (savedCart != null) {
            return savedCart;
        }
        throw new ErrorDomain(ErrorCode.DATABASE_ERROR_WITH_SAVE_CART,requestDto);
    }


    @Transactional
    public MyCartsResponseDto updateCarts(String userId, UpdateCartsRequestDto requestDto) {
        List<CartDomain> userCarts = cartPersistenceAdapter.findByUserId(userId);

        Set<String> updateCartIds = requestDto.getCarts().stream()
                .map(UpdateCartRequestDto::getId)
                .collect(Collectors.toSet());

        List<CartDomain> deletedCarts = userCarts.stream()
                .filter(cart -> !updateCartIds.contains(cart.getId()))
                .collect(Collectors.toList());

        Set<String> originCartIds = userCarts.stream()
                .map(CartDomain::getId)
                .collect(Collectors.toSet());

        List<UpdateCartRequestDto> createdCarts = requestDto.getCarts().stream()
                .filter(cart -> !originCartIds.contains(cart.getId()))
                .collect(Collectors.toList());

        deletedCarts.forEach((cartDomain -> {
            this.deleteCart(userId,cartDomain.getId());
        }));

        createdCarts.forEach((cartDomain -> {
            CreateCartRequestDto cartRequestDto = new CreateCartRequestDto();
            cartRequestDto.setName(cartDomain.getName());
            this.createCart(userId,cartRequestDto);
        }));

        List<CartDomain> updatedUserCarts = cartPersistenceAdapter.findByUserId(userId);
        for (int i = 0; i < requestDto.getCarts().size(); i++) {
            UpdateCartRequestDto cartRequestDto = requestDto.getCarts().get(i);
            int finalI = i;
            updatedUserCarts.stream()
                    .filter(cart -> cart.getId().equals(cartRequestDto.getId()))
                    .findFirst()
                    .ifPresent(cart -> {
                        cart.setOrderIndex(finalI + 1);
                        cart.setName(cartRequestDto.getName());
                        cartPersistenceAdapter.save(cart);
                    });
        }

        return getMyCarts(userId);
    }
}
