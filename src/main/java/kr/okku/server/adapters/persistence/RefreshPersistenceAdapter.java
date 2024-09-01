package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.refresh.RefreshEntity;
import kr.okku.server.adapters.persistence.repository.refresh.RefreshRepository;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.mapper.CartMapper;
import kr.okku.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshPersistenceAdapter {

    private final RefreshRepository refreshRepository;

    public RefreshPersistenceAdapter(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    public void save(String refreshToken) {
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefreshToken(refreshToken);
        refreshRepository.save(refreshEntity);
    }

    public void update(String beforeToken, String newToken){
        RefreshEntity refreshEntity = refreshRepository.findById(beforeToken).get();
        refreshEntity.setRefreshToken(newToken);
        refreshRepository.save(refreshEntity);
    }

    public boolean isExist(String refreshToken){
       return refreshRepository.existsByRefreshToken(refreshToken);
    }

}
