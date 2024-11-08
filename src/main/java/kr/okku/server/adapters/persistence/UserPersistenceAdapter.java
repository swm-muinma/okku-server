package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.cart.CartEntity;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.mapper.CartMapper;
import kr.okku.server.mapper.PickMapper;
import kr.okku.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserPersistenceAdapter {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<UserDomain> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    public UserDomain save(UserDomain userDomain) {
        UserEntity userEntity = UserMapper.toEntity(userDomain);
        UserEntity savedEntity = userRepository.save(userEntity);
        return UserMapper.toDomain(savedEntity);
    }


    public Optional<String> getRole(String id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        return Optional.ofNullable(userEntity.getRole());
    }

    public Optional<UserDomain> findById(String id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        return Optional.ofNullable(UserMapper.toDomain(userEntity));
    }

    public Optional<UserDomain> findByKakaoId(String kakaoId) {
        UserEntity userEntity = userRepository.findByKakaoId(kakaoId).orElse(null);
        return Optional.ofNullable(UserMapper.toDomain(userEntity));
    }

    public Optional<UserDomain> findByAppleId(String appleId) {
        UserEntity userEntity = userRepository.findByAppleId(appleId).orElse(null);
        return Optional.ofNullable(UserMapper.toDomain(userEntity));
    }

    public void deleteById(String userId) {
        userRepository.deleteById(userId);
    }
}
