package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.mapper.CartMapper;
import kr.okku.server.mapper.PickMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartPersistenceAdapter {

    private final CartRepository cartRepository;

    public CartPersistenceAdapter(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CartDomain save(CartDomain cartDomain) {
        CartEntity cartEntity = CartMapper.toEntity(cartDomain);
        CartEntity savedEntity = cartRepository.save(cartEntity);
        return CartMapper.toDomain(savedEntity);
    }

    public Optional<CartDomain> findById(String id){
        CartEntity cartEntity = cartRepository.findById(id).get();
        return Optional.ofNullable(CartMapper.toDomain(cartEntity));
    }

    public  List<CartDomain> findByPickItemIdsIn(List<String> pickItemIds){
        return cartRepository.findByPickItemIdsIn(pickItemIds)
                .stream()
                .map(CartMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<CartDomain> findByUserId(String userId){
        return  cartRepository.findByUserIdOrderByOrderIndexAsc(userId)
                .stream()
                .map(CartMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteById(String cartId) {
        cartRepository.deleteById(cartId);
    }
}
