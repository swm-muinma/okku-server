package kr.okku.server.controller;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.controller.user.UpdateProfileRequest;
import kr.okku.server.dto.controller.user.UserResponse;
import kr.okku.server.service.PickService;
import kr.okku.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse> getProfile(@RequestHeader("userId") String userId) {
        UserDomain user = userService.getProfile(userId);
        UserResponse response = new UserResponse(user.getId(), user.getName(), user.getHeight(), user.getWeight(), user.getForm());
        return ResponseEntity.ok(response);
    }

    // Update user profile
    @PatchMapping
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("userId") String userId,
            @RequestBody UpdateProfileRequest request
    ) {
        UserDomain updatedUser = userService.updateProfile(userId, request.getName(), request.getHeight(), request.getWeight(), request.getForm());
        UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getHeight(), updatedUser.getWeight(), updatedUser.getForm());
        return ResponseEntity.ok(response);
    }

    // Withdraw user account
    @GetMapping("/withdraw")
    public ResponseEntity<Void> withdrawAccount(@RequestHeader("userId") String userId) {
        userService.withdrawAccount(userId);
        return ResponseEntity.ok().build();
    }
}
