package kr.okku.server.controller;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.service.PickService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picks")
public class PickController {

    private final PickService pickService;

    public PickController(PickService pickService) {
        this.pickService = pickService;
    }

    @PostMapping
    public ResponseEntity<PickDomain> createPick(@RequestParam String userId, @RequestParam String url) {
        return ResponseEntity.ok(pickService.createPick(userId, url));
    }

    // 기타 필요한 엔드포인트 추가
}
