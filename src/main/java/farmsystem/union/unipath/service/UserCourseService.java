package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import farmsystem.union.unipath.dto.UserCourseHistoryRequest;
import farmsystem.union.unipath.repository.UserCourseHistoryRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCourseService {

    private final UserCourseHistoryRepository userCourseHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveUserCourseHistory(UserCourseHistoryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        for (UserCourseHistoryRequest.CourseInfo course : request.getCourses()) {
            UserCourseHistory history = UserCourseHistory.builder()
                    .user(user)
                    .classId(course.getClassId())
                    .className(course.getClassName())
                    .semesterId(request.getSemesterId()) // 수정된 필드
                    .build();

            userCourseHistoryRepository.save(history);
        }
    }
}