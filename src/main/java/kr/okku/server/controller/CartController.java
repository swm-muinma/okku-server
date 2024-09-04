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
    public ResponseEntity<MyCartsResponseDto> getMyCarts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        MyCartsResponseDto carts = cartService.getMyCarts(userId);
        return ResponseEntity.ok(carts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        cartService.deleteCart(userId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CreateCartResponseDto> createCart(
            @RequestBody CreateCartRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CartDomain savedCart = cartService.createCart(userId, request.getName(), request.getPickIds());
        CreateCartResponseDto response = new CreateCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());
        return ResponseEntity.ok(response);
    }
}
