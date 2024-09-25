package kr.okku.server.controller;

import kr.okku.server.service.FittingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/fitting")
public class FittingController {

    private final FittingService fittingService;

    @Autowired
    public FittingController(FittingService fittingService) {
        this.fittingService = fittingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getItemInfoWithoutLogin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile image,
            @RequestParam("imageId") String pickId) {
        try {
            String userId = userDetails.getUsername();
            var result = fittingService.fitting(userId, image,pickId);
            System.out.println("Request successful - Fitting with pickId: " + pickId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - Fitting with pickId: %s, Error: %s%n", pickId, e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
