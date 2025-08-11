package farmsystem.union.unipath.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.domain.Lecture;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import farmsystem.union.unipath.dto.CourseDTO;
import farmsystem.union.unipath.dto.LectureDTO;
import farmsystem.union.unipath.dto.OptimizerResultDTO;
import farmsystem.union.unipath.dto.ScheduleResponseDTO;
import farmsystem.union.unipath.dto.UserInputDTO;
import farmsystem.union.unipath.repository.CourseRepository;
import farmsystem.union.unipath.repository.LectureRepository;
import farmsystem.union.unipath.repository.UserCourseHistoryRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * OR-Tools(Python)를 이용한 시간표 최적화 로직을 처리하는 서비스입니다.
 * unipath-backend 프로젝트 구조에 맞게 최종 수정되었습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizerService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;
    private final UserCourseHistoryRepository userCourseHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;


    /**
     * 사용자의 요구사항을 기반으로 최적의 시간표를 생성하고, 프론트엔드에 전달할 최종 DTO로 가공합니다.
     * @param memberId 사용자 ID
     * @param userInput 사용자의 요구사항 (필수 과목, 목표 학점 등)
     * @return 프론트엔드 전달용으로 가공된 최종 시간표 DTO
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDTO generateSchedule(Long memberId, UserInputDTO userInput) {

        // 1. DB에서 사용자 정보 조회
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + memberId));

        // 1-1. 사용자의 직업 및 전공 정보 확인
        if (user.getSelectedCareerGroup() == null || user.getSelectedCareerGroup().getName().isEmpty()) {
            throw new IllegalArgumentException("추천을 받으려면 먼저 직업을 설정해야 합니다.");
        }
        String jobGroup = user.getSelectedCareerGroup().getName();
        userInput.setJobGroup(jobGroup);

        // 2. 사용자가 이미 수강한 과목 목록을 조회합니다.
        List<UserCourseHistory> completedCourses = userCourseHistoryRepository.findByUser(user);
        List<String> completedCourseIds = completedCourses.stream()
                .map(UserCourseHistory::getClassId)
                .collect(Collectors.toList());
        userInput.setCompletedCourseIds(completedCourseIds);

        // 3. DB에서 데이터 준비: 이번 학기에 개설된 모든 강의와 시간 정보를 조회합니다.
        List<Lecture> allLectures = lectureRepository.findAllWithLectureTimes();

        // 3-1. (중요) 조회된 강의가 없으면, 더 이상 진행하지 않고 즉시 반환합니다.
        if (allLectures.isEmpty()) {
            log.warn("데이터베이스에서 고려할 강의를 찾지 못했습니다. 시간표를 생성할 수 없습니다.");
            return ScheduleResponseDTO.builder()
                    .status("FAILED")
                    .message("시간표를 생성하는 데 사용할 수 있는 강의 데이터가 없습니다. 데이터베이스를 확인해주세요.")
                    .schedule(Collections.emptyList())
                    .build();
        }

        List<String> courseIds = allLectures.stream()
                .map(lecture -> lecture.getCourse().getClassId())
                .distinct()
                .collect(Collectors.toList());
        List<Course> allCourses = courseRepository.findAllById(courseIds);

        // 4. Python 스크립트에 전달할 데이터(JSON)를 생성합니다.
        Map<String, Object> optimizerInputMap = new HashMap<>();
        optimizerInputMap.put("lectures", allLectures.stream().map(LectureDTO::fromEntity).collect(Collectors.toList()));
        optimizerInputMap.put("courses", allCourses.stream().map(CourseDTO::fromEntity).collect(Collectors.toList()));
        optimizerInputMap.put("user_input", userInput);

        // 4-1. 직업군별 과목 점수 데이터를 생성합니다.
        Map<String, Integer> courseScores = getCourseScoresByJobGroup(allCourses, jobGroup);
        optimizerInputMap.put("course_scores", courseScores);


        try {
            // 5. 외부 프로세스로 Python 스크립트를 실행합니다.
            Resource scriptResource = resourceLoader.getResource("classpath:scripts/optimizer.py");
            String scriptPath = scriptResource.getFile().getAbsolutePath();

            // 가상 환경의 Python 실행 파일 경로를 동적으로 구성합니다.
            String projectRoot = new File("").getAbsolutePath();
            String pythonExecutable = projectRoot + "/venv/bin/python3";

            // 가상 환경의 Python이 존재하지 않을 경우, 시스템 기본 'python3'를 사용합니다.
            if (!new File(pythonExecutable).exists()) {
                log.warn("가상 환경의 Python을 찾을 수 없습니다: {}. 기본 'python3'를 사용합니다.", pythonExecutable);
                pythonExecutable = "python3";
            } else {
                log.info("가상 환경의 Python을 사용합니다: {}", pythonExecutable);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);
            Process process = processBuilder.start();

            String jsonInputForPython = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(optimizerInputMap);
            log.info("--- Sending JSON to Python Script ---\n{}", jsonInputForPython);

            // 6. 생성한 JSON 데이터를 Python 스크립트의 표준 입력(stdin)으로 전달합니다.
            try (OutputStream os = process.getOutputStream()) {
                objectMapper.writeValue(os, optimizerInputMap);
            }

            // 7. Python 스크립트의 표준 출력(stdout) 결과를 읽어옵니다.
            StringBuilder resultJson = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(resultJson::append);
            }

            // 8. (디버깅용) Python 스크립트 실행 중 발생한 에러 로그를 확인합니다.
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                errorReader.lines().forEach(line -> {
                    log.error("Optimizer Script Error: {}", line);
                    errorOutput.append(line).append("\n");
                });
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script exited with non-zero code: {}", exitCode);
                throw new RuntimeException("시간표 생성 스크립트 실행에 실패했습니다. 에러: " + errorOutput.toString());
            }

            // 9. 반환받은 JSON 결과를 내부 처리용 DTO로 변환합니다.
            OptimizerResultDTO pythonResult = objectMapper.readValue(resultJson.toString(), OptimizerResultDTO.class);

            // 10. Python 결과를 최종 응답 DTO로 변환합니다.
            if ("SUCCESS".equals(pythonResult.getStatus())) {
                // 추천된 강의 ID 목록을 추출합니다.
                List<Long> recommendedLectureIds = pythonResult.getLectureIds();

                // ID를 이용해 DB에서 전체 강의 정보(시간 정보 포함)를 다시 조회합니다.
                List<Lecture> recommendedLectures = lectureRepository.findAllByIdWithLectureTimes(recommendedLectureIds);

                // 엔티티를 최종 응답 DTO로 변환합니다.
                List<ScheduleResponseDTO.RecommendedLectureDTO> scheduleDto = recommendedLectures.stream()
                        .map(ScheduleResponseDTO.RecommendedLectureDTO::from)
                        .collect(Collectors.toList());

                return ScheduleResponseDTO.builder()
                        .status("SUCCESS")
                        .schedule(scheduleDto)
                        .build();
            } else {
                // 실패 시, 실패 상태와 메시지를 담아 반환합니다.
                return ScheduleResponseDTO.builder()
                        .status(pythonResult.getStatus()) // e.g., "FAILED"
                        .message(pythonResult.getMessage())
                        .schedule(Collections.emptyList())
                        .build();
            }

        } catch (Exception e) {
            log.error("Failed to execute optimizer script", e);
            throw new RuntimeException("시간표 생성 중 내부 오류가 발생했습니다.", e);
        }
    }

    /**
     * 직업군 이름에 따라 Course 엔티티에서 적절한 점수 필드를 선택하고,
     * (학수번호, 점수) 형태의 Map으로 변환하여 반환합니다.
     * @param courses 점수를 추출할 Course 목록
     * @param jobGroup 사용자 직업군 이름
     * @return (학수번호, 점수) Map
     */
    private Map<String, Integer> getCourseScoresByJobGroup(List<Course> courses, String jobGroup) {
        Map<String, Function<Course, Integer>> scoreAccessors = new HashMap<>();
        scoreAccessors.put("웹 개발", Course::getScoreWebDev);
        scoreAccessors.put("앱 개발", Course::getScoreAppDev);
        scoreAccessors.put("게임 개발", Course::getScoreGameDev);
        scoreAccessors.put("데이터/AI 개발", Course::getScoreDataAiDev);
        scoreAccessors.put("정보보안", Course::getScoreInfosec);
        scoreAccessors.put("클라우드/DevOps", Course::getScoreCloudDevops);
        scoreAccessors.put("기술 기획/관리", Course::getScoreTechPm);
        scoreAccessors.put("특수 기술/기타", Course::getScoreSpecialTech);

        Function<Course, Integer> scoreAccessor = scoreAccessors.get(jobGroup);
        if (scoreAccessor == null) {
            log.warn("'{}'에 해당하는 직업군 점수 컬럼을 찾을 수 없습니다. 기본 점수(0)를 사용합니다.", jobGroup);
            return Collections.emptyMap();
        }

        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getClassId,
                        course -> {
                            Integer score = scoreAccessor.apply(course);
                            return score != null ? score : 0; // 점수가 null이면 0점으로 처리
                        }
                ));
    }
}
