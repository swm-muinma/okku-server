package kr.okku.server.adapters.persistence.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    List<UserEntity> findByUserId(String userId);

    // 필요한 커스텀 메소드 추가
}
