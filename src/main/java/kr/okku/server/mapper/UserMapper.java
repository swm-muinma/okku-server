package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.domain.UserDomain;

public class UserMapper {

    // UserEntity <-> UserDomain
    public static UserDomain toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return UserDomain.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .image(userEntity.getImage())
                .height(userEntity.getHeight())
                .weight(userEntity.getWeight())
                .form(userEntity.getForm())
                .isPremium(userEntity.getIsPremium())
                .kakaoId(userEntity.getKakaoId())
                .appleId(userEntity.getAppleId())
                .build();
    }

    public static UserEntity toEntity(UserDomain userDomain) {
        if (userDomain == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDomain.getId());
        userEntity.setName(userDomain.getName());
        userEntity.setImage(userDomain.getImage());
        userEntity.setHeight(userDomain.getHeight());
        userEntity.setWeight(userDomain.getWeight());
        userEntity.setForm(userDomain.getForm());
        userEntity.setIsPremium(userDomain.getIsPremium());
        userEntity.setKakaoId(userDomain.getKakaoId());
        userEntity.setAppleId(userDomain.getAppleId());
        return userEntity;
    }
}