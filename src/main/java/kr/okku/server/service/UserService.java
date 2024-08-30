package kr.okku.server.service;

import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createDummyUser(String name, String image, String height, String weight, String form, Boolean isPremium, String kakaoId, String appleId) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString()); // Generate a new ID
        user.setName(name);
        user.setImage(image);
        user.setHeight(height);
        user.setWeight(weight);
        user.setForm(form);
        user.setIsPremium(isPremium);
        user.setKakaoId(kakaoId);
        user.setAppleId(appleId);
        user.setCreatedAt(new Date()); // Set created date
        user.setUpdatedAt(new Date()); // Set updated date

        return userRepository.save(user); // Save user to the database
    }

    public UserEntity getUserById(String userId){
        return userRepository.findById(userId).get();
    }
}
