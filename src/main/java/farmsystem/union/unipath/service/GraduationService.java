package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.CohortGraduationCredits;
import farmsystem.union.unipath.domain.Course;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.domain.UserCourseHistory;
import farmsystem.union.unipath.dto.CreditInfoDTO;
import farmsystem.union.unipath.dto.GraduationStatusDTO;
import farmsystem.union.unipath.repository.CohortGraduationCreditsRepository;
import farmsystem.union.unipath.repository.CourseRepository;
import farmsystem.union.unipath.repository.UserCourseHistoryRepository;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GraduationService {

    private final UserRepository userRepository;
    private final UserCourseHistoryRepository userCourseHistoryRepository;
    private final CohortGraduationCreditsRepository cohortGraduationCreditsRepository;
    private final CourseRepository courseRepository;

    private static final Set<String> COMMON_GENERAL_COURSES = new HashSet<>(Arrays.asList(
            "자아와명상1", "자아와명상2", "불교와인간", "커리어디자인", "소셜앙트레프레너십과리더십", "글로벌앙트레프레너십과리더십", "테크노앙트레프레너십과리더십", "EAS1", "EAS2", "기술보고서작성및발표", "지혜와자비명작세미나", "존재와역사명작세미나", "경제와사회명작세미나", "자연과기술명작세미나", "문화와예술명작세미나", "기업가정신과리더십", "Global English1", "Global English2", "Business English1", "Business English2", "디지털시대의글쓰기", "디지털 기술과 사회의 이해", "프로그래밍 이해와 실습", "빅데이터와 인공지능의 이해"
    ));

    private static final Set<String> BASIC_EDUCATION_COURSES = new HashSet<>(Arrays.asList(
            "기술창조와특허", "공학경제", "공학윤리"
    ));


    @Transactional(readOnly = true)
    public GraduationStatusDTO getGraduationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        System.out.println("DEBUG: User ID: " + userId + ", Admission Year from DB: '" + user.getAdmissionYear() + "'");

        // 1. 사용자의 입학년도를 졸업요건 키로 변환
        String cohortKey = convertToCohortKey(user.getAdmissionYear());

        System.out.println("DEBUG: Converted Cohort Key: '" + cohortKey + "'");

        // 2. 변환된 키로 졸업 요건 조회
        Optional<CohortGraduationCredits> cohortCreditsOpt = cohortGraduationCreditsRepository.findById(cohortKey);

        if (cohortCreditsOpt.isEmpty()) {
            System.out.println("DEBUG: Graduation requirements NOT FOUND for key: '" + cohortKey + "'");
            return createEmptyGraduationStatus(); // 해당 학번의 졸업요건 데이터가 없으면 빈 현황 반환
        }
        
        System.out.println("DEBUG: Graduation requirements FOUND for key: '" + cohortKey + "'");
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
            String courseName = course.getClassName();

            if (COMMON_GENERAL_COURSES.contains(courseName)) {
                commonGeneralCurrent += credits;
            } else if (BASIC_EDUCATION_COURSES.contains(courseName)) {
                basicEducationCurrent += credits;
            } else {
                String division = course.getRequiredDivision() != null ? course.getRequiredDivision() : "";
                switch (division) {
                    case "BSM": bsmCurrent += credits; break;
                    case "전공기초": case "전공전문": majorCurrent += credits; break;
                }
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

    @Transactional(readOnly = true)
    public List<CreditInfoDTO> getGraduationStatusAsList(Long userId) {
        GraduationStatusDTO status = getGraduationStatus(userId);
        List<CreditInfoDTO> list = new ArrayList<>();

        list.add(new CreditInfoDTO("총 학점", status.getTotalCredits().getCurrent(), status.getTotalCredits().getRequired()));
        list.add(new CreditInfoDTO("공통교양", status.getCommonGeneralEducationCredits().getCurrent(), status.getCommonGeneralEducationCredits().getRequired()));
        list.add(new CreditInfoDTO("기본소양", status.getBasicEducationCredits().getCurrent(), status.getBasicEducationCredits().getRequired()));
        list.add(new CreditInfoDTO("BSM", status.getBsmCredits().getCurrent(), status.getBsmCredits().getRequired()));
        list.add(new CreditInfoDTO("전공", status.getMajorCredits().getCurrent(), status.getMajorCredits().getRequired()));

        return list;
    }

    /**
     * 사용자의 입학년도(예: " 21 ", "2021")를 졸업요건 테이블의 키(예: "20~22")로 변환하는 메서드
     */
    private String convertToCohortKey(String admissionYear) {
        if (admissionYear == null || admissionYear.trim().isEmpty()) {
            return "default"; // 예외 처리: null이거나 비어있는 문자열
        }

        String trimmedAdmissionYear = admissionYear.trim();
        int year;
        try {
            year = Integer.parseInt(trimmedAdmissionYear);
        } catch (NumberFormatException e) {
            return trimmedAdmissionYear; // 숫자로 변환할 수 없으면 원래 값을 그대로 반환
        }

        // 2자리 학번(예: 21, 22)을 4자리 연도(예: 2021, 2022)로 변환
        if (year < 100) {
            year += 2000;
        }

        if (year >= 2020 && year <= 2022) {
            return "20~22";
        } else if (year == 2023) {
            return "23";
        } else if (year == 2024) {
            return "24";
        } else if (year == 2025) {
            return "25";
        }

        // 규칙에 해당하지 않는 다른 연도는 그대로 문자열로 반환
        return String.valueOf(year);
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
