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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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

        // --- 여기가 핵심 수정 포인트 ---
        // 2. 요청으로 들어온 과목 리스트에서 중복을 제거
        List<UserCourseHistoryRequest.CourseInfo> distinctCourses = request.getCourses().stream()
                .filter(distinctByKey(UserCourseHistoryRequest.CourseInfo::getClassId))
                .collect(Collectors.toList());

        // 3. 중복이 제거된 새로운 수강 기록 저장
        for (UserCourseHistoryRequest.CourseInfo courseInfo : distinctCourses) {
            UserCourseHistory newHistory = UserCourseHistory.builder()
                    .user(user)
                    .classId(courseInfo.getClassId())
                    .className(courseInfo.getClassName())
                    .semesterId(request.getSemesterId())
                    .build();

            userCourseHistoryRepository.save(newHistory);
        }
    }

    // Stream에서 중복을 제거하기 위한 유틸리티 메서드
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCourseHistoryBySemester(Long userId, int semesterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        List<UserCourseHistory> histories = userCourseHistoryRepository.findByUserAndSemesterId(user, semesterId);

        return histories.stream()
                .map(history -> {
                    Course course = courseRepository.findById(history.getClassId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목 코드입니다: " + history.getClassId()));
                    return new CourseResponse(course);
                })
                .collect(Collectors.toList());
    }
}

