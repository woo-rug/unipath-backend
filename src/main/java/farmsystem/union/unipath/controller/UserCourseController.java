package farmsystem.union.unipath.controller;

import farmsystem.union.unipath.dto.UserCourseHistoryRequest;
import farmsystem.union.unipath.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-courses")
@RequiredArgsConstructor
public class UserCourseController {

    private final UserCourseService userCourseService;

    @PostMapping("/save-history")
    public ResponseEntity<String> saveCourseHistory(@RequestBody UserCourseHistoryRequest request) {
        userCourseService.saveUserCourseHistory(request);
        return ResponseEntity.ok("User course history saved successfully.");
    }
}