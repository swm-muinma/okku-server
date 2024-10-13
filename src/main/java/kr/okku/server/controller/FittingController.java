package kr.okku.server.controller;

import kr.okku.server.dto.controller.fitting.FittingRequestDto;
import kr.okku.server.dto.controller.fitting.FittingResultDto;
import kr.okku.server.dto.controller.fitting.GetFittingListResponseDto;
import kr.okku.server.service.FittingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fitting")
public class FittingController {

    private final FittingService fittingService;

    @Autowired
    public FittingController(FittingService fittingService) {
        this.fittingService = fittingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute FittingRequestDto requestDto) {
            String userId = userDetails.getUsername();
        System.out.println("fitting");
        System.out.println(requestDto);
            var result = fittingService.fitting(userId, requestDto);
            return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<GetFittingListResponseDto> getFittingList(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        System.out.println("get fitting");
        GetFittingListResponseDto result = fittingService.getFittingList(userId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/recent")
    public ResponseEntity<FittingResultDto> getRecentlyFittingItem(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        System.out.println("get recently fitting");
        FittingResultDto result = fittingService.getNowOne(userId);
        return ResponseEntity.ok(result);
    }
}
