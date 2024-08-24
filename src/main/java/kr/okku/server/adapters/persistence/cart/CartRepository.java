package kr.okku.server.adapters.persistence.cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<CartEntity, String> {
    List<CartEntity> findByUserId(String userId);

    // 필요한 커스텀 메소드 추가
}
