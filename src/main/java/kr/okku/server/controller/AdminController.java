package kr.okku.server.controller;

import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.admin.FiittingListResponseDto;
import kr.okku.server.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<FiittingListResponseDto>> fiittingList(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        List<FiittingListResponseDto> responseDto = adminService.getFiittingList(userId);
        return ResponseEntity.ok(responseDto);

    }

    @GetMapping("/retention")
    public ResponseEntity<Boolean> getRetention(@AuthenticationPrincipal UserDetails userDetails) throws ParseException {
        String userId = userDetails.getUsername();
        adminService.getRetentionStats();
        return ResponseEntity.ok(true);

    }
}
