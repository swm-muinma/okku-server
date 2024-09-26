package kr.okku.server.service;

import kr.okku.server.adapters.oauth.apple.AppleOauthAdapter;
import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.user.SetFcmTokenRequestDto;
import kr.okku.server.dto.controller.user.SetFcmTokenResponseDto;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import kr.okku.server.enums.FormEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserPersistenceAdapter userPersistenceAdapter;

    private final PickPersistenceAdapter pickPersistenceAdapter;

    private final CartPersistenceAdapter cartPersistenceAdapter;
    private final AppleOauthAdapter appleOauthAdapter;

    @Autowired
    public UserService(CartPersistenceAdapter cartPersistenceAdapter, PickPersistenceAdapter pickPersistenceAdapter,
                       UserPersistenceAdapter userPersistenceAdapter, AppleOauthAdapter appleOauthAdapter) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.appleOauthAdapter = appleOauthAdapter;
    }

    // Retrieve user profile by user ID
    public UserDomain getProfile(String userId) {
        UserDomain user = userPersistenceAdapter.findById(userId).get();
        if (user == null) {
            throw new ErrorDomain(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    // Update user profile
    @Transactional
    public UserDomain updateProfile(String id, String name, Integer height, Integer weight, FormEnum form) {
        if (form != null) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
        }

        UserDomain user = UserDomain.builder()
                .name(name)
                .id(id)
                .height(height)
                .weight(weight)
                .form(form)
                .build();
        UserDomain updatedUser = userPersistenceAdapter.save(user);
        return updatedUser;
    }

    @Transactional
    public SetFcmTokenResponseDto addFcmToken(String userId, String fcmTokens) {

        UserDomain user = userPersistenceAdapter.findById(userId).orElse(null);
        if(user==null){
            throw new ErrorDomain(ErrorCode.USER_NOT_FOUND);
        }
        user.addFcmToken(fcmTokens);
        UserDomain updatedUser = userPersistenceAdapter.save(user);
        return new SetFcmTokenResponseDto(updatedUser.getFcmToken());
    }

    // Withdraw user account
    @Transactional
    public boolean withdrawAccount(String userId,String platform, String code) {
        if(platform=="apple"){
            AppleTokenResponseDto authToken = appleOauthAdapter.getAppleAuthToken(code);
            appleOauthAdapter.revoke(authToken);
        }
        List<PickDomain> picks = pickPersistenceAdapter.findByUserId(userId);
        List<String> pickIds = picks.stream().map(PickDomain::getId).collect(Collectors.toList());

        pickPersistenceAdapter.deleteByIdIn(pickIds);
        this.deletePickFromAllCart(pickIds);

        cartPersistenceAdapter.findByUserId(userId).forEach(cart -> {
            if (cart.getId() != null) {
                cartPersistenceAdapter.deleteById(cart.getId());
            }
        });

        userPersistenceAdapter.deleteById(userId);
        return true;
    }

    public String checkAccountSocial(String userId){
        UserDomain user = userPersistenceAdapter.findById(userId).orElse(null);
//        if(user.getAppleId()!=null && user.getAppleId()!=""){
        if (user != null && user.getAppleId() != null && !user.getAppleId().isEmpty()) {
            return "apple";
        }
        return "kakao";
    }

    @Transactional
    public List<String> deletePickFromAllCart(List<String> pickIds) {
        List<CartDomain> carts = cartPersistenceAdapter.findByPickItemIdsIn(pickIds);
        if (carts.isEmpty()) {
            return null;
        }

        for (CartDomain cart : carts) {
            cart.deletePicks(pickIds);
            cartPersistenceAdapter.save(cart);
        }

        return pickIds;
    }
}
