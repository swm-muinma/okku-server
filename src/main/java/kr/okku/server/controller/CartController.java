package kr.okku.server.controller;

import kr.okku.server.domain.CartDomain;
import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.cart.*;
import kr.okku.server.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<MyCartsResponseDto> getMyCarts(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","GET",null,"요청 시작").toJson());
            MyCartsResponseDto carts = cartService.getMyCarts(traceId,userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","GET",null,"요청 종료").toJson());
            return ResponseEntity.ok(carts);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts/"+id,"DELETE",null,"요청 시작").toJson());
            cartService.deleteCart(traceId,userId, id);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts/"+id,"DELETE",null,"요청 종료").toJson());
            return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CreateCartResponseDto> createCart(
            @RequestBody CreateCartRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","POST",request,"요청 시작").toJson());
        CartDomain savedCart = cartService.createCart(traceId,userId, request);
            CreateCartResponseDto response = new CreateCartResponseDto(savedCart.getId(), savedCart.getName(), savedCart.getPickItemIds());

        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","POST",null,"요청 종료").toJson());
            return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<MyCartsResponseDto> updateCarts(
            @RequestBody UpdateCartsRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","PATCH",request,"요청 시작").toJson());
        MyCartsResponseDto savedCart = cartService.updateCarts(traceId,userId, request);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/carts","PATCH",null,"요청 종료").toJson());
        return ResponseEntity.ok(savedCart);
    }

}
