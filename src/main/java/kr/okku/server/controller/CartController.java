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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    // Get My Carts - List the carts of the user with pagination
    @GetMapping
    public ResponseEntity<MyCartsResponseDto> getMyCarts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("userId") String userId // Assuming the userId is passed as a header
    ) {
        MyCartsResponseDto carts = cartService.getMyCarts(userId, page, size);
        return ResponseEntity.ok(carts);
    }

    // Delete a Cart - Delete a specific cart by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(
            @PathVariable String id,
            @RequestHeader("userId") String userId // Assuming the userId is passed as a header
    ) {
        cartService.deleteCart(userId, id);
        return ResponseEntity.ok().build();
    }

    // Create a Cart - Create a new cart for the user
    @PostMapping
    public ResponseEntity<CreateCartResponseDto> createCart(
            @RequestBody CreateCartRequestDto request,
            @RequestHeader("userId") String userId // Assuming the userId is passed as a header
    ) {
        CartDomain savedCart = cartService.createCart(userId, request.getName(), request.getPickIds());
        CreateCartResponseDto response = new CreateCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());
        return ResponseEntity.ok(response);
    }
}
