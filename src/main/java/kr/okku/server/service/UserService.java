package kr.okku.server.service;

import kr.okku.server.adapters.oauth.apple.AppleOauthAdapter;
import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.user.SetFcmTokenRequestDto;
import kr.okku.server.dto.controller.user.SetFcmTokenResponseDto;
import kr.okku.server.dto.controller.user.UpdateProfileRequestDto;
import kr.okku.server.dto.controller.user.UserImagesResponseDto;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import kr.okku.server.enums.FormEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    public String migration(){
        List<UserDomain> users = userPersistenceAdapter.findAll();
        for (UserDomain user : users) {
            Set<String> fcmTokens = user.getFcmToken();

            // fcmToken이 비어있지 않으면 첫 번째 값을 가져와 singleFcmToken에 할당
            if (fcmTokens != null && !fcmTokens.isEmpty()) {
                String firstToken = fcmTokens.iterator().next();
                user.setSingleFcmToken(firstToken);
            } else {
                // 기본값을 설정하고 싶다면, 예를 들어 "default_token"
                user.setSingleFcmToken("default_token");
            }

            // 변경된 user 객체를 저장
            userPersistenceAdapter.save(user);
        }
        return "";
    }

    public UserDomain getProfile(TraceId traceId,String userId) {
        UserDomain user = userPersistenceAdapter.findById(userId).get();
        if (user == null) {
            throw new ErrorDomain(ErrorCode.USER_NOT_FOUND,traceId);
        }
        return user;
    }

    public UserImagesResponseDto getUserImages(String userId){
        UserDomain user = userPersistenceAdapter.findById(userId).get();
        UserImagesResponseDto responseDto = new UserImagesResponseDto();
        List<String> reverseList = new ArrayList<>(user.getUserImages());
        Collections.reverse(reverseList);
        responseDto.setImages(reverseList);
        return responseDto;
    }

    @Transactional
    public UserDomain updateProfile(TraceId traceId,String id, UpdateProfileRequestDto requestDto) {
        String name=requestDto.getName();
        Integer height=requestDto.getHeight();
        Integer weight=requestDto.getWeight();
        FormEnum form =requestDto.getForm();
        if (form == null) {
            throw new ErrorDomain(ErrorCode.FORM_IS_EMPTY,traceId);
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
    public SetFcmTokenResponseDto addFcmToken(TraceId traceId,String userId, SetFcmTokenRequestDto requestDto) {

        String fcmTokens = requestDto.getFcmToken();
        UserDomain user = userPersistenceAdapter.findById(userId).orElse(null);
        if(user==null){
            throw new ErrorDomain(ErrorCode.USER_NOT_FOUND,traceId);
        }
        user.addFcmToken(fcmTokens);
        user.setSingleFcmToken(fcmTokens);
        UserDomain updatedUser = userPersistenceAdapter.save(user);
        return new SetFcmTokenResponseDto(updatedUser.getFcmTokensForArray());
    }

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
