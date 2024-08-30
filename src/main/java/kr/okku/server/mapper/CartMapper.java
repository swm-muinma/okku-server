package kr.okku.server.mapper;

import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import kr.okku.server.domain.CartDomain;

import java.util.Arrays;

public class CartMapper {

    // CartEntity <-> CartDomain
    public static CartDomain toDomain(CartEntity cartEntity) {
        if (cartEntity == null) {
            return null;
        }
        return CartDomain.builder()
                .id(cartEntity.getId())
                .userId(cartEntity.getUserId())
                .name(cartEntity.getName())
                .pickNum(cartEntity.getPickNum())
                .pickItemIds(Arrays.stream(cartEntity.getPickItemIds()).toList())
                .build();
    }

    public static CartEntity toEntity(CartDomain cartDomain) {
        if (cartDomain == null) {
            return null;
        }
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId(cartDomain.getId());
        cartEntity.setUserId(cartDomain.getUserId());
        cartEntity.setName(cartDomain.getName());
        cartEntity.setPickNum(cartDomain.getPickNum());
        cartEntity.setPickItemIds(cartDomain.getPickItemIds().toArray(new String[0]));
        return cartEntity;
    }

}
