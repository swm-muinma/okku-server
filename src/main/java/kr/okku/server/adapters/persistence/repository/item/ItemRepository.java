package kr.okku.server.adapters.persistence.repository.item;
import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<ItemEntity, String> {
    Optional<ItemEntity> findByUrl(String url);
}
