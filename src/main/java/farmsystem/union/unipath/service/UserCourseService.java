package farmsystem.union.unipath.service;

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

        // 1. 해당 학기(semesterId)의 기존 수강 기록을 모두 삭제
        userCourseHistoryRepository.deleteByUserAndSemesterId(user, request.getSemesterId());

        // 2. 새로운 수강 기록 저장
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
                    // CourseRepository를 사용하여 학수번호에 해당하는 Course 엔티티를 찾아야 합니다.
                    // CourseResponse가 학점 정보도 필요하므로, 이 로직이 필요합니다.
                    // 현재 코드에는 CourseResponse에 credits 필드가 없습니다.
                    // CourseResponse를 수정하거나, CourseHistory에 credits 필드를 추가하는 것이 좋습니다.
                    return new CourseResponse(courseRepository.findById(history.getClassId()).get());
                })
                .collect(Collectors.toList());
    }
}