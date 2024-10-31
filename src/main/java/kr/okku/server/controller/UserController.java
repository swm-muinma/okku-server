package kr.okku.server.controller;
import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.fitting.FittingRequestDto;
import kr.okku.server.dto.controller.user.*;
import kr.okku.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Retrieve user profile
    @GetMapping
    public ResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users","GET",null,"요청 시작").toJson());
        UserDomain user = userService.getProfile(userId);
            UserResponseDto response = new UserResponseDto(user.getId(), user.getName(), user.getHeight(), user.getWeight(), user.getForm());

        log.info("{}",new ControllerLogEntity(traceId,userId,"/users","GET",null,"요청 종료").toJson());
            return ResponseEntity.ok(response);
    }


    @GetMapping("/images")
    public ResponseEntity<UserImagesResponseDto> getCachingImage(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/images","GET",null,"요청 시작").toJson());
        UserImagesResponseDto result = userService.getUserImages(userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/images","GET",null,"요청 종료").toJson());
        return ResponseEntity.ok(result);
    }

    // Update user profile
    @PatchMapping
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users","PATCH",null,"요청 시작").toJson());
            UserDomain updatedUser = userService.updateProfile(userId, request);
            UserResponseDto response = new UserResponseDto(updatedUser.getId(), updatedUser.getName(), updatedUser.getHeight(), updatedUser.getWeight(), updatedUser.getForm());

        log.info("{}",new ControllerLogEntity(traceId,userId,"/users","PATCH",null,"요청 종료").toJson());
            return ResponseEntity.ok(response);
    }

    @PatchMapping("/fcmtoken")
    public ResponseEntity<SetFcmTokenResponseDto> addFcmToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SetFcmTokenRequestDto request
    ) {
        String userId = userDetails.getUsername();

        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/fcmtoken","PATCH",request,"요청 시작").toJson());
        System.out.println(request);
        SetFcmTokenResponseDto response = userService.addFcmToken(userId, request);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/fcmtoken","PATCH",null,"요청 종료").toJson());
        return ResponseEntity.ok(response);

    }

    // Withdraw user account
    @GetMapping("/pre-withdraw")
    public ResponseEntity<String> withdrawCheck(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/pre-withdraw","GET",null,"요청 시작").toJson());
        String res = userService.checkAccountSocial(userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/pre-withdraw","GET",null,"요청 종료").toJson());
        return ResponseEntity.ok(res);

    }

    @GetMapping("/withdraw/{platform}")
    public ResponseEntity<Void> withdrawAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String platform,
            @RequestParam String code
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/withdraw","GET",null,"요청 시작").toJson());
        userService.withdrawAccount(userId, platform, code);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/users/withdraw","GET",null,"요청 종료").toJson());
        return ResponseEntity.ok().build();
    }
}
