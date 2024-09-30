package kr.okku.server.service;

import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.dto.controller.cart.CartDto;
import kr.okku.server.dto.controller.cart.CreateCartRequestDto;
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

    public MyCartsResponseDto getMyCarts(String userId) {
        List<CartDomain> cartDomains = cartPersistenceAdapter.findByUserId(userId);

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
            throw new ErrorDomain(ErrorCode.NOT_OWNER,null);
        }
        cartPersistenceAdapter.deleteById(cartId);
        return cartId;
    }

    @Transactional
    public CartDomain createCart(String userId, CreateCartRequestDto requestDto) {
        String name = requestDto.getName();
        List<String> pickIds = requestDto.getPickIds();
        if (name == null || name.isEmpty()) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS,requestDto);
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

        CartDomain savedCart = cartPersistenceAdapter.save(cart);
        if (savedCart != null) {
            return savedCart;
        }
        throw new ErrorDomain(ErrorCode.INVALID_PARAMS,requestDto);
    }
}
