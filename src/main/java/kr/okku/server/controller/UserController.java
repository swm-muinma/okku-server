package kr.okku.server.controller;

import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserEntity> createDummyUser(
            @RequestParam String name,
            @RequestParam String image,
            @RequestParam String height,
            @RequestParam String weight,
            @RequestParam String form,
            @RequestParam Boolean isPremium,
            @RequestParam String kakaoId,
            @RequestParam String appleId
    ) {
        UserEntity createdUser = userService.createDummyUser(name, image, height, weight, form, isPremium, kakaoId, appleId);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("")
    public ResponseEntity<UserEntity> getUser(@RequestParam String userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
