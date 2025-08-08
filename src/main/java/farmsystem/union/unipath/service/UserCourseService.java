package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import farmsystem.union.unipath.dto.CourseResponse;
import farmsystem.union.unipath.dto.UserCourseHistoryRequest;
import farmsystem.union.unipath.repository.CourseRepository;
import farmsystem.union.unipath.repository.UserCourseHistoryRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCourseService {

    private final UserCourseHistoryRepository userCourseHistoryRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void saveUserCourseHistory(UserCourseHistoryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        userCourseHistoryRepository.deleteByUserAndSemesterId(user, request.getSemesterId());

        for (UserCourseHistoryRequest.CourseInfo courseInfo : request.getCourses()) {
            UserCourseHistory newHistory = UserCourseHistory.builder()
                    .user(user)
                    .classId(courseInfo.getClassId())
                    .className(courseInfo.getClassName())
                    .semesterId(request.getSemesterId())
                    .build();

            userCourseHistoryRepository.save(newHistory);
        }
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCourseHistoryBySemester(Long userId, int semesterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        List<UserCourseHistory> histories = userCourseHistoryRepository.findByUserAndSemesterId(user, semesterId);

        return histories.stream()
                .map(history -> {
                    // CourseRepository를 사용하여 Course 엔티티를 조회하고, 새 생성자로 DTO 생성
                    Course course = courseRepository.findById(history.getClassId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목 코드입니다: " + history.getClassId()));
                    return new CourseResponse(course);
                })
                .collect(Collectors.toList());
    }
}