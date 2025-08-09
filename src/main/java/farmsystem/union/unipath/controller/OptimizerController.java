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

@RestController
@RequestMapping("/optimizer")
@RequiredArgsConstructor
public class OptimizerController {

    private final OptimizerService optimizerService;

    @PostMapping("/recommend")
    public ResponseEntity<ScheduleResponseDTO> recommendSchedule(@RequestBody UserInputDTO userInputDTO) {
        // TODO: 추후 Spring Security 등 인증 기능 구현 후 실제 사용자 ID를 동적으로 받아와야 합니다.
        Long memberId = 1L; // 임시 사용자 ID

        ScheduleResponseDTO result = optimizerService.generateSchedule(memberId, userInputDTO);
        return ResponseEntity.ok(result);
    }
}
