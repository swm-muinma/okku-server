package kr.okku.server.controller;

import kr.okku.server.domain.CartDomain;
import kr.okku.server.dto.controller.admin.FiittingListResponseDto;
import kr.okku.server.dto.controller.cart.CreateCartRequestDto;
import kr.okku.server.dto.controller.cart.CreateCartResponseDto;
import kr.okku.server.dto.controller.cart.MyCartsResponseDto;
import kr.okku.server.dto.controller.cart.UpdateCartsRequestDto;
import kr.okku.server.service.AdminService;
import kr.okku.server.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<FiittingListResponseDto> fiittingList(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        FiittingListResponseDto responseDto = adminService.getFiittingList(userId);
        return ResponseEntity.ok(responseDto);

    }
}
