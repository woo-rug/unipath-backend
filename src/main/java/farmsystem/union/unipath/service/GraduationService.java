package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.CohortGraduationCredits;
import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import farmsystem.union.unipath.dto.GraduationStatusDTO;
import farmsystem.union.unipath.repository.CohortGraduationCreditsRepository;
import farmsystem.union.unipath.repository.CourseRepository;
import farmsystem.union.unipath.repository.UserCourseHistoryRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GraduationService {

    private final UserRepository userRepository;
    private final UserCourseHistoryRepository userCourseHistoryRepository;
    private final CohortGraduationCreditsRepository cohortGraduationCreditsRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public GraduationStatusDTO getGraduationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // 1. 사용자의 입학년도를 졸업요건 키로 변환
        String cohortKey = convertToCohortKey(user.getAdmissionYear());

        // 2. 변환된 키로 졸업 요건 조회
        Optional<CohortGraduationCredits> cohortCreditsOpt = cohortGraduationCreditsRepository.findById(cohortKey);

        if (cohortCreditsOpt.isEmpty()) {
            return createEmptyGraduationStatus(); // 해당 학번의 졸업요건 데이터가 없으면 빈 현황 반환
        }
        CohortGraduationCredits cohortCredits = cohortCreditsOpt.get();

        // 3. 사용자의 전체 수강 기록 조회
        List<UserCourseHistory> allHistories = userCourseHistoryRepository.findByUser(user);
        if (allHistories.isEmpty()) {
            return createEmptyGraduationStatus(cohortCredits); // 수강 기록 없으면 현재 학점 0으로 표시
        }

        // 4. 각 영역별 이수 학점 계산
        int totalCurrent = 0;
        int commonGeneralCurrent = 0;
        int basicEducationCurrent = 0;
        int bsmCurrent = 0;
        int majorCurrent = 0;

        for (UserCourseHistory history : allHistories) {
            Optional<Course> courseOpt = courseRepository.findById(history.getClassId());
            if (courseOpt.isEmpty()) continue;

            Course course = courseOpt.get();
            int credits;
            try {
                credits = Integer.parseInt(course.getCredits());
            } catch (NumberFormatException e) {
                continue;
            }

            totalCurrent += credits;
            String division = course.getRequiredDivision() != null ? course.getRequiredDivision() : "";
            switch (division) {
                case "공통교양": commonGeneralCurrent += credits; break;
                case "기본소양": basicEducationCurrent += credits; break;
                case "BSM": bsmCurrent += credits; break;
                case "전공기초": case "전공전문": majorCurrent += credits; break;
            }
        }

        // 5. 최종 DTO 생성 및 반환
        return GraduationStatusDTO.builder()
                .totalCredits(new GraduationStatusDTO.CreditStatus(totalCurrent, cohortCredits.getTotalCreditsRequired()))
                .commonGeneralEducationCredits(new GraduationStatusDTO.CreditStatus(commonGeneralCurrent, cohortCredits.getCommonGeneralEducationCredits()))
                .basicEducationCredits(new GraduationStatusDTO.CreditStatus(basicEducationCurrent, cohortCredits.getBasicEducationCredits()))
                .bsmCredits(new GraduationStatusDTO.CreditStatus(bsmCurrent, cohortCredits.getBsmCredits()))
                .majorCredits(new GraduationStatusDTO.CreditStatus(majorCurrent, cohortCredits.getMajorCredits()))
                .build();
    }

    /**
     * 사용자의 입학년도(예: "2021")를 졸업요건 테이블의 키(예: "20-22")로 변환하는 메서드
     */
    private String convertToCohortKey(String admissionYear) {
        if (admissionYear == null || admissionYear.length() != 4) {
            return "default"; // 예외 처리
        }

        int year = Integer.parseInt(admissionYear);

        if (year >= 2020 && year <= 2022) {
            return "20-22";
        } else if (year == 2023) {
            return "23";
        } else if (year == 2024) {
            return "24";
        } else if (year == 2025) {
            return "25";
        }
        // 다른 학번대에 대한 규칙이 있다면 여기에 추가
        // else if (year >= 2026 && year <= 2028) {
        //     return "26-28";
        // }

        return admissionYear; // 규칙이 없으면 원래 연도 그대로 반환
    }

    // 이수 기록이 없을 때, 기준 학점만 표시하는 DTO 생성
    private GraduationStatusDTO createEmptyGraduationStatus(CohortGraduationCredits cohortCredits) {
        return GraduationStatusDTO.builder()
                .totalCredits(new GraduationStatusDTO.CreditStatus(0, cohortCredits.getTotalCreditsRequired()))
                .commonGeneralEducationCredits(new GraduationStatusDTO.CreditStatus(0, cohortCredits.getCommonGeneralEducationCredits()))
                .basicEducationCredits(new GraduationStatusDTO.CreditStatus(0, cohortCredits.getBasicEducationCredits()))
                .bsmCredits(new GraduationStatusDTO.CreditStatus(0, cohortCredits.getBsmCredits()))
                .majorCredits(new GraduationStatusDTO.CreditStatus(0, cohortCredits.getMajorCredits()))
                .build();
    }

    // 졸업요건 데이터 자체가 없을 때, 모든 학점을 0으로 표시하는 DTO 생성
    private GraduationStatusDTO createEmptyGraduationStatus() {
        return GraduationStatusDTO.builder()
                .totalCredits(new GraduationStatusDTO.CreditStatus(0, 0))
                .commonGeneralEducationCredits(new GraduationStatusDTO.CreditStatus(0, 0))
                .basicEducationCredits(new GraduationStatusDTO.CreditStatus(0, 0))
                .bsmCredits(new GraduationStatusDTO.CreditStatus(0, 0))
                .majorCredits(new GraduationStatusDTO.CreditStatus(0, 0))
                .build();
    }
}