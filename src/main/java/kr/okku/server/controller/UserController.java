package kr.okku.server.controller;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.user.UpdateProfileRequest;
import kr.okku.server.dto.controller.user.UserResponse;
import kr.okku.server.service.PickService;
import kr.okku.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        UserDomain user = userService.getProfile(userId);
        UserResponse response = new UserResponse(user.getId(), user.getName(), user.getHeight(), user.getWeight(), user.getForm());
        return ResponseEntity.ok(response);
    }

    // Update user profile
    @PatchMapping
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request
    ) {
        String userId = userDetails.getUsername();
        UserDomain updatedUser = userService.updateProfile(userId, request.getName(), request.getHeight(), request.getWeight(), request.getForm());
        UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getHeight(), updatedUser.getWeight(), updatedUser.getForm());
        return ResponseEntity.ok(response);
    }

    // Withdraw user account
    @GetMapping("/pre-withdraw")
    public ResponseEntity<String> withdrawCheck(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        String res = userService.checkAccountSocial(userId);
        return ResponseEntity.ok(res);
    }
    @GetMapping("/withdraw/{platform}")
    public ResponseEntity<Void> withdrawAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String platform,
            @RequestParam String code) {
        String userId = userDetails.getUsername();
        userService.withdrawAccount(userId,platform,code);
        return ResponseEntity.ok().build();
    }

}
