package kr.okku.server.adapters.persistence.repository.item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<ItemEntity, String> {

    // 필요한 커스텀 메소드 추가
}
