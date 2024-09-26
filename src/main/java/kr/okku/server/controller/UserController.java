package kr.okku.server.controller;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.user.SetFcmTokenRequestDto;
import kr.okku.server.dto.controller.user.SetFcmTokenResponseDto;
import kr.okku.server.dto.controller.user.UpdateProfileRequest;
import kr.okku.server.dto.controller.user.UserResponse;
import kr.okku.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Retrieve user profile
    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        try {
            UserDomain user = userService.getProfile(userId);
            UserResponse response = new UserResponse(user.getId(), user.getName(), user.getHeight(), user.getWeight(), user.getForm());
            System.out.printf("Request successful - UserId: %s, Name: %s%n", userId, user.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Update user profile
    @PatchMapping
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request
    ) {
        String userId = userDetails.getUsername();
        try {
            UserDomain updatedUser = userService.updateProfile(userId, request.getName(), request.getHeight(), request.getWeight(), request.getForm());
            UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getHeight(), updatedUser.getWeight(), updatedUser.getForm());
            System.out.printf("Request successful - UserId: %s, Updated Name: %s%n", userId, request.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/fcmtoken")
    public ResponseEntity<SetFcmTokenResponseDto> addFcmToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SetFcmTokenRequestDto request
    ) {
        String userId = userDetails.getUsername();
        try {
            SetFcmTokenResponseDto response = userService.addFcmToken(userId, request.getFcmToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Withdraw user account
    @GetMapping("/pre-withdraw")
    public ResponseEntity<String> withdrawCheck(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        try {
            String res = userService.checkAccountSocial(userId);
            System.out.printf("Request successful - UserId: %s, Pre-withdraw check passed%n", userId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/withdraw/{platform}")
    public ResponseEntity<Void> withdrawAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String platform,
            @RequestParam String code
    ) {
        String userId = userDetails.getUsername();
        try {
            userService.withdrawAccount(userId, platform, code);
            System.out.printf("Request successful - UserId: %s, Platform: %s%n", userId, platform);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Platform: %s, Error: %s%n", userId, platform, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
