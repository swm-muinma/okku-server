package kr.okku.server.mapper;

import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import kr.okku.server.domain.CartDomain;

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
                .pickItemIds(cartEntity.getPickItemIds())
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
        cartEntity.setPickItemIds(cartDomain.getPickItemIds());
        return cartEntity;
    }

}
