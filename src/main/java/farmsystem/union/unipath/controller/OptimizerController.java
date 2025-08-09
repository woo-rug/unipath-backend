package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.ScheduleResponseDTO;
import farmsystem.union.unipath.dto.UserInputDTO;
import farmsystem.union.unipath.service.OptimizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable; // PathVariable 추가

@RestController
@RequestMapping("/optimizer")
@RequiredArgsConstructor
public class OptimizerController {

    private final OptimizerService optimizerService;

    // ⭐️ [핵심 수정] URL에 userId를 포함하고, @PathVariable을 통해 실제 사용자 ID를 받도록 변경
    @PostMapping("/recommend/{userId}")
    public ResponseEntity<ScheduleResponseDTO> recommendSchedule(
            @PathVariable Long userId,
            @RequestBody UserInputDTO userInputDTO) {

        ScheduleResponseDTO result = optimizerService.generateSchedule(userId, userInputDTO);
        return ResponseEntity.ok(result);
    }
}
