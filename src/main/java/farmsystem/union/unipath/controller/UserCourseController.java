package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.CourseResponse;
import farmsystem.union.unipath.dto.UserCourseHistoryRequest;
import farmsystem.union.unipath.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-courses")
@RequiredArgsConstructor
public class UserCourseController {

    private final UserCourseService userCourseService;

    // 수강 기록 저장 API
    @PostMapping("/save-history")
    public ResponseEntity<String> saveCourseHistory(@RequestBody UserCourseHistoryRequest request) {
        userCourseService.saveUserCourseHistory(request);
        return ResponseEntity.ok("User course history saved successfully.");
    }

    // 특정 학기 수강 과목 조회 API (새로 추가)
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourseHistoryBySemester(
            @RequestParam Long userId,
            @RequestParam int semesterId) {
        List<CourseResponse> courses = userCourseService.getCourseHistoryBySemester(userId, semesterId);
        return ResponseEntity.ok(courses);
    }
}