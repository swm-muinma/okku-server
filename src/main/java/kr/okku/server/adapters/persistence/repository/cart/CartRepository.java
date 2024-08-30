package kr.okku.server.adapters.persistence.repository.cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<CartEntity, String> {
    List<CartEntity> findByUserId(String userId);

    Optional<CartEntity> findById(String id);

    void deleteById(String cartId);

    // 필요한 커스텀 메소드 추가
}
