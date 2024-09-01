package kr.okku.server.adapters.persistence.repository.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findById(String userId);

    Optional<UserEntity>  findByKakaoId(String userId);

    // 필요한 커스텀 메소드 추가
}
