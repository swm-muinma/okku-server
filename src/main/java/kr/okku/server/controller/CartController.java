package kr.okku.server.controller;

import kr.okku.server.domain.CartDomain;
import kr.okku.server.dto.controller.cart.*;
import kr.okku.server.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<MyCartsResponseDto> getMyCarts(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
            MyCartsResponseDto carts = cartService.getMyCarts(userId);
            System.out.printf("Request successful - Get carts for userId: %s%n", userId);
            return ResponseEntity.ok(carts);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
            cartService.deleteCart(userId, id);
            System.out.printf("Request successful - Deleted cart with id: %s for userId: %s%n", id, userId);
            return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CreateCartResponseDto> createCart(
            @RequestBody CreateCartRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
            CartDomain savedCart = cartService.createCart(userId, request);
            CreateCartResponseDto response = new CreateCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());
            System.out.printf("Request successful - Created cart for userId: %s with name: %s%n", userId, request.getName());
            return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<RenameCartResponseDto> renameCart(
            @RequestBody RenameCartRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CartDomain savedCart = cartService.renameCart(userId, request);
        RenameCartResponseDto response = new RenameCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());
        System.out.printf("Request successful - Rename cart for userId: %s%n", userId);
        return ResponseEntity.ok(response);
    }
}
