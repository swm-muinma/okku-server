package kr.okku.server.adapters.persistence.repository.refresh;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends MongoRepository<RefreshEntity, String> {
    Optional<RefreshEntity> findByRefreshToken(String refreshToken);

    boolean existsByRefreshToken(String refreshToken);
}
