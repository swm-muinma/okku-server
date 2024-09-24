package kr.okku.server.controller;

import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.dto.controller.cart.CartDto;
import kr.okku.server.dto.controller.cart.CreateCartRequestDto;
import kr.okku.server.dto.controller.cart.CreateCartResponseDto;
import kr.okku.server.dto.controller.cart.MyCartsResponseDto;
import kr.okku.server.dto.controller.pick.DeletePicksRequest;
import kr.okku.server.dto.controller.pick.MovePicksRequest;
import kr.okku.server.dto.controller.pick.UserPicksResponseDTO;
import kr.okku.server.service.CartService;
import kr.okku.server.service.Oauth2Service;
import kr.okku.server.service.PickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        try {
            MyCartsResponseDto carts = cartService.getMyCarts(userId);
            System.out.printf("Request successful - Get carts for userId: %s%n", userId);
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            System.err.printf("Request failed - Get carts for userId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        try {
            cartService.deleteCart(userId, id);
            System.out.printf("Request successful - Deleted cart with id: %s for userId: %s%n", id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.printf("Request failed - Delete cart with id: %s for userId: %s, Error: %s%n", id, userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<CreateCartResponseDto> createCart(
            @RequestBody CreateCartRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        try {
            CartDomain savedCart = cartService.createCart(userId, request.getName(), request.getPickIds());
            CreateCartResponseDto response = new CreateCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());
            System.out.printf("Request successful - Created cart for userId: %s with name: %s%n", userId, request.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.printf("Request failed - Create cart for userId: %s, Error: %s%n", userId, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
