package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.*;
import farmsystem.union.unipath.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final CareerGroupRepository careerGroupRepository;
    private final CareerRepository careerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionWeightRepository questionWeightRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CohortGraduationCreditsRepository cohortGraduationCreditsRepository;
    private final RequirementGroupsRepository requirementGroupsRepository;
    private final GroupCoursesRepository groupCoursesRepository;
    private final CohortRequirementGroupsRepository cohortRequirementGroupsRepository;
    private final PrerequisiteCourseRepository prerequisiteCourseRepository;

    @PostConstruct
    @Transactional
    public void init() {
        // --- 1. 직업군 데이터 로드 (중복 실행 방지) ---
        if (careerGroupRepository.count() == 0) {
            careerGroupRepository.saveAll(List.of(
                    CareerGroup.builder().name("웹 개발").description("웹사이트와 웹 애플리케이션을 만드는 직업군입니다.").build(),
                    CareerGroup.builder().name("앱 개발").description("모바일 앱을 만드는 직업군입니다.").build(),
                    CareerGroup.builder().name("게임 개발").description("게임 개발에 특화된 직업군입니다.").build(),
                    CareerGroup.builder().name("데이터/AI 개발").description("데이터 분석 및 인공지능 기술을 다루는 직업군입니다.").build(),
                    CareerGroup.builder().name("정보보안").description("시스템 및 네트워크 보안을 담당하는 직업군입니다.").build(),
                    CareerGroup.builder().name("클라우드/DevOps").description("클라우드 환경 구축 및 운영을 담당하는 직업군입니다.").build(),
                    CareerGroup.builder().name("기술 기획/관리").description("개발 프로젝트의 기획, 관리 및 품질 보증을 담당하는 직업군입니다.").build(),
                    CareerGroup.builder().name("특수 기술/기타").description("블록체인, 양자컴퓨팅 등 특수 기술 분야를 다루는 직업군입니다.").build()
            ));
        }

        // --- 2. 직업 데이터 로드 (중복 실행 방지) ---
        if (careerRepository.count() == 0) {
            Map<String, CareerGroup> careerGroupMap = StreamSupport.stream(careerGroupRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(CareerGroup::getName, group -> group));

            careerRepository.saveAll(List.of(
                    Career.builder().name("프론트엔드 개발자").description("사용자에게 보이는 웹 화면을 개발하고, 웹 인터페이스를 구현합니다.").careerGroup(careerGroupMap.get("웹 개발")).build(),
                    Career.builder().name("백엔드 개발자").description("서버와 데이터베이스를 구축하고 관리하여 웹 서비스의 핵심 기능을 구현합니다.").careerGroup(careerGroupMap.get("웹 개발")).build(),
                    Career.builder().name("풀스택 개발자").description("프론트엔드와 백엔드 개발을 모두 수행하며 웹 서비스 전체를 관리합니다.").careerGroup(careerGroupMap.get("웹 개발")).build(),
                    Career.builder().name("iOS 개발자").description("애플 iOS 운영체제 기반의 모바일 애플리케이션을 개발합니다.").careerGroup(careerGroupMap.get("앱 개발")).build(),
                    Career.builder().name("Android 개발자").description("구글 안드로이드 운영체제 기반의 모바일 애플리케이션을 개발합니다.").careerGroup(careerGroupMap.get("앱 개발")).build(),
                    Career.builder().name("크로스플랫폼 개발자").description("iOS와 Android에서 모두 작동하는 모바일 앱을 개발합니다.").careerGroup(careerGroupMap.get("앱 개발")).build(),
                    Career.builder().name("클라이언트 개발자").description("게임의 그래픽, 사용자 인터페이스, 플레이어 로직 등 눈에 보이는 부분을 개발합니다.").careerGroup(careerGroupMap.get("게임 개발")).build(),
                    Career.builder().name("서버 개발자").description("게임 서버를 구축하여 사용자 데이터와 게임 룰을 관리하는 역할을 합니다.").careerGroup(careerGroupMap.get("게임 개발")).build(),
                    Career.builder().name("게임 기획자").description("게임의 컨셉, 스토리, 규칙 등을 설계하여 개발 프로세스를 이끕니다.").careerGroup(careerGroupMap.get("게임 개발")).build(),
                    Career.builder().name("데이터 분석가").description("데이터를 수집, 분석하여 사업적인 인사이트를 도출하고 의사결정을 돕습니다.").careerGroup(careerGroupMap.get("데이터/AI 개발")).build(),
                    Career.builder().name("데이터 엔지니어").description("방대한 데이터를 효율적으로 처리하고 저장하는 시스템을 구축하고 관리합니다.").careerGroup(careerGroupMap.get("데이터/AI 개발")).build(),
                    Career.builder().name("AI/머신러닝 개발자").description("머신러닝 모델을 개발하고 AI 기반 서비스를 구축하는 역할을 합니다.").careerGroup(careerGroupMap.get("데이터/AI 개발")).build(),
                    Career.builder().name("화이트 해커(보안 컨설턴트)").description("시스템의 취약점을 찾아내고 보안 위협에 대한 해결책을 제시합니다.").careerGroup(careerGroupMap.get("정보보안")).build(),
                    Career.builder().name("시스템 보안 개발자").description("안전한 시스템 및 소프트웨어를 개발하고 보안 기능을 설계합니다.").careerGroup(careerGroupMap.get("정보보안")).build(),
                    Career.builder().name("웹 보안 전문가").description("웹사이트와 웹 애플리케이션의 보안 취약점을 진단하고 방어하는 전문가입니다.").careerGroup(careerGroupMap.get("정보보안")).build(),
                    Career.builder().name("DevOps 엔지니어").description("개발(Dev)과 운영(Ops)의 협업을 촉진하고 자동화하여 효율성을 높입니다.").careerGroup(careerGroupMap.get("클라우드/DevOps")).build(),
                    Career.builder().name("클라우드 엔지니어").description("AWS, Azure, GCP 등 클라우드 플랫폼을 활용하여 인프라를 구축하고 운영합니다.").careerGroup(careerGroupMap.get("클라우드/DevOps")).build(),
                    Career.builder().name("인프라 엔지니어").description("서버, 네트워크 등 시스템의 기반 환경을 구축하고 안정적으로 관리합니다.").careerGroup(careerGroupMap.get("클라우드/DevOps")).build(),
                    Career.builder().name("기술 PM").description("기술적인 지식을 바탕으로 프로젝트의 일정, 자원, 팀원을 관리하고 목표를 달성합니다.").careerGroup(careerGroupMap.get("기술 기획/관리")).build(),
                    Career.builder().name("정보보안 관리자").description("기업의 정보보안 정책을 수립하고 보안 시스템을 관리하는 역할을 합니다.").careerGroup(careerGroupMap.get("기술 기획/관리")).build(),
                    Career.builder().name("QA 엔지니어").description("제품의 품질을 보증하기 위해 다양한 테스트를 계획하고 수행하며 품질 개선을 주도합니다.").careerGroup(careerGroupMap.get("기술 기획/관리")).build(),
                    Career.builder().name("블록체인 개발자").description("분산원장기술(DLT)을 활용하여 암호화폐, 스마트 컨트랙트 등을 개발합니다.").careerGroup(careerGroupMap.get("특수 기술/기타")).build(),
                    Career.builder().name("양자컴퓨팅 연구원").description("양자역학 원리를 이용한 새로운 컴퓨팅 기술을 연구하고 개발합니다.").careerGroup(careerGroupMap.get("특수 기술/기타")).build(),
                    Career.builder().name("개발 문서 전문가").description("기술 문서를 작성하고 관리하여 개발자와 사용자 간의 소통을 돕습니다.").careerGroup(careerGroupMap.get("특수 기술/기타")).build()
            ));
        }

        // --- 3. 질문, 가중치 데이터 로드 (중복 실행 방지) ---
        if (questionRepository.count() == 0) {
            Map<String, CareerGroup> careerGroupMap = StreamSupport.stream(careerGroupRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(CareerGroup::getName, group -> group));

            Question q1 = questionRepository.save(Question.builder().question("나는 복잡한 문제를 해결하기 위해 논리적으로 접근하는 것을 즐긴다.").build());
            Question q2 = questionRepository.save(Question.builder().question("새로운 기술이나 이론을 배우는 것에 대해 강한 호기심을 느낀다.").build());
            Question q3 = questionRepository.save(Question.builder().question("다른 사람들과 함께 목표를 달성하기 위해 협력하는 것을 선호한다.").build());
            Question q4 = questionRepository.save(Question.builder().question("보이지 않는 시스템의 내부 구조를 설계하고 최적화하는 것에 흥미가 있다.").build());
            Question q5 = questionRepository.save(Question.builder().question("깔끔하고 사용하기 쉬운 사용자 인터페이스를 만드는 것에 관심이 많다.").build());
            Question q6 = questionRepository.save(Question.builder().question("사소한 오류나 버그를 찾아내고 수정하는 일에 끈기가 있다.").build());
            Question q7 = questionRepository.save(Question.builder().question("새로운 아이디어를 내고, 이를 실제 결과물로 만드는 것을 좋아한다.").build());
            Question q8 = questionRepository.save(Question.builder().question("데이터 속에서 의미 있는 패턴이나 규칙을 찾아내는 것에 재미를 느낀다.").build());
            Question q9 = questionRepository.save(Question.builder().question("다양한 사람들의 의견을 듣고 종합하여 더 나은 방향을 제시하는 편이다.").build());
            Question q10 = questionRepository.save(Question.builder().question("최신 트렌드를 빠르게 파악하고, 이를 적용하는 것을 좋아한다.").build());

            questionWeightRepository.saveAll(List.of(
                    new QuestionWeight(q1, careerGroupMap.get("웹 개발"), 2), new QuestionWeight(q1, careerGroupMap.get("게임 개발"), 1), new QuestionWeight(q1, careerGroupMap.get("데이터/AI 개발"), 3), new QuestionWeight(q1, careerGroupMap.get("정보보안"), 3), new QuestionWeight(q1, careerGroupMap.get("클라우드/DevOps"), 2),
                    new QuestionWeight(q2, careerGroupMap.get("앱 개발"), 1), new QuestionWeight(q2, careerGroupMap.get("게임 개발"), 2), new QuestionWeight(q2, careerGroupMap.get("데이터/AI 개발"), 2), new QuestionWeight(q2, careerGroupMap.get("특수 기술/기타"), 3),
                    new QuestionWeight(q3, careerGroupMap.get("웹 개발"), 1), new QuestionWeight(q3, careerGroupMap.get("게임 개발"), 2), new QuestionWeight(q3, careerGroupMap.get("클라우드/DevOps"), 3), new QuestionWeight(q3, careerGroupMap.get("기술 기획/관리"), 3),
                    new QuestionWeight(q4, careerGroupMap.get("웹 개발"), 3), new QuestionWeight(q4, careerGroupMap.get("앱 개발"), 2), new QuestionWeight(q4, careerGroupMap.get("정보보안"), 1), new QuestionWeight(q4, careerGroupMap.get("클라우드/DevOps"), 2),
                    new QuestionWeight(q5, careerGroupMap.get("웹 개발"), 3), new QuestionWeight(q5, careerGroupMap.get("앱 개발"), 3), new QuestionWeight(q5, careerGroupMap.get("게임 개발"), 2),
                    new QuestionWeight(q6, careerGroupMap.get("앱 개발"), 1), new QuestionWeight(q6, careerGroupMap.get("데이터/AI 개발"), 2), new QuestionWeight(q6, careerGroupMap.get("정보보안"), 3), new QuestionWeight(q6, careerGroupMap.get("기술 기획/관리"), 2),
                    new QuestionWeight(q7, careerGroupMap.get("웹 개발"), 2), new QuestionWeight(q7, careerGroupMap.get("앱 개발"), 2), new QuestionWeight(q7, careerGroupMap.get("게임 개발"), 3), new QuestionWeight(q7, careerGroupMap.get("특수 기술/기타"), 2),
                    new QuestionWeight(q8, careerGroupMap.get("데이터/AI 개발"), 3), new QuestionWeight(q8, careerGroupMap.get("정보보안"), 2),
                    new QuestionWeight(q9, careerGroupMap.get("게임 개발"), 1), new QuestionWeight(q9, careerGroupMap.get("클라우드/DevOps"), 2), new QuestionWeight(q9, careerGroupMap.get("기술 기획/관리"), 3),
                    new QuestionWeight(q10, careerGroupMap.get("웹 개발"), 2), new QuestionWeight(q10, careerGroupMap.get("앱 개발"), 2), new QuestionWeight(q10, careerGroupMap.get("특수 기술/기타"), 2)
            ));
        }

        // --- 4. 졸업 요건 학점 데이터 로드 (중복 실행 방지) ---
        if (cohortGraduationCreditsRepository.count() == 0) {
            cohortGraduationCreditsRepository.saveAll(List.of(
                    CohortGraduationCredits.builder().admissionYear("20~22").totalCreditsRequired(140).commonGeneralEducationCredits(14).basicEducationCredits(9).bsmCredits(21).majorCredits(84).build(),
                    CohortGraduationCredits.builder().admissionYear("23").totalCreditsRequired(130).commonGeneralEducationCredits(17).basicEducationCredits(9).bsmCredits(21).majorCredits(84).build(),
                    CohortGraduationCredits.builder().admissionYear("24").totalCreditsRequired(130).commonGeneralEducationCredits(25).basicEducationCredits(9).bsmCredits(21).majorCredits(84).build(),
                    CohortGraduationCredits.builder().admissionYear("25").totalCreditsRequired(140).commonGeneralEducationCredits(25).basicEducationCredits(6).bsmCredits(21).majorCredits(72).build()
            ));
        }

        // --- 5. 요건 그룹 데이터 로드 (중복 실행 방지) ---
        if (requirementGroupsRepository.count() == 0) {
            requirementGroupsRepository.saveAll(List.of(
                    RequirementGroups.builder().groupName("동국인성").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("대학생활탐구").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("기업가정신과 리더십").selectionType("SELECT_ONE_OF_THREE").build(),
                    RequirementGroups.builder().groupName("영어").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("글쓰기").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("세미나(1)").selectionType("SELECT_ONE_OF_FIVE").build(),
                    RequirementGroups.builder().groupName("자기계발").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("사고와소통(영어)").selectionType("SELECT_ONE_OF_FOUR").build(),
                    RequirementGroups.builder().groupName("사고와소통(글쓰기)").selectionType("SELECT_ONE_OF_TWO").build(),
                    RequirementGroups.builder().groupName("디지털리터러시").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("기본소양(9)").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("기본소양(6)").selectionType("SELECT_TWO_OF_THREE").build(),
                    RequirementGroups.builder().groupName("BSM(수학)").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("BSM(이산수학)").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("BSM(과학)").selectionType("SELECT_ONE_OF_SIX").build(),
                    RequirementGroups.builder().groupName("전공필수").selectionType("MANDATORY").build(),
                    RequirementGroups.builder().groupName("전공필수(이산구조)").selectionType("MANDATORY").build()
            ));
        }

        // --- 6. 그룹 과목 연결 데이터 로드 (중복 실행 방지) ---
        if (groupCoursesRepository.count() == 0) {
            Map<String, Course> courseMap = StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(Course::getClassId, course -> course));

            Map<String, RequirementGroups> groupMap = StreamSupport.stream(requirementGroupsRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(RequirementGroups::getGroupName, group -> group));

            groupCoursesRepository.saveAll(List.of(
                    new GroupCourses(groupMap.get("동국인성"), courseMap.get("RGC0017")),
                    new GroupCourses(groupMap.get("동국인성"), courseMap.get("RGC0018")),
                    new GroupCourses(groupMap.get("동국인성"), courseMap.get("RGC0003")),
                    new GroupCourses(groupMap.get("대학생활탐구"), courseMap.get("RGC1074")),
                    new GroupCourses(groupMap.get("기업가정신과 리더십"), courseMap.get("RGC1050")),
                    new GroupCourses(groupMap.get("기업가정신과 리더십"), courseMap.get("RGC1051")),
                    new GroupCourses(groupMap.get("기업가정신과 리더십"), courseMap.get("RGC1052")),
                    new GroupCourses(groupMap.get("영어"), courseMap.get("RGC1080")),
                    new GroupCourses(groupMap.get("영어"), courseMap.get("RGC1081")),
                    new GroupCourses(groupMap.get("글쓰기"), courseMap.get("RGC0005")),
                    new GroupCourses(groupMap.get("세미나(1)"), courseMap.get("RGC1014")),
                    new GroupCourses(groupMap.get("세미나(1)"), courseMap.get("RGC1010")),
                    new GroupCourses(groupMap.get("세미나(1)"), courseMap.get("RGC1011")),
                    new GroupCourses(groupMap.get("세미나(1)"), courseMap.get("RGC1012")),
                    new GroupCourses(groupMap.get("세미나(1)"), courseMap.get("RGC1013")),
                    new GroupCourses(groupMap.get("자기계발"), courseMap.get("RGC1074")),
                    new GroupCourses(groupMap.get("자기계발"), courseMap.get("RGC1053")),
                    new GroupCourses(groupMap.get("사고와소통(영어)"), courseMap.get("RGC1082")),
                    new GroupCourses(groupMap.get("사고와소통(영어)"), courseMap.get("RGC1083")),
                    new GroupCourses(groupMap.get("사고와소통(영어)"), courseMap.get("RGC1084")),
                    new GroupCourses(groupMap.get("사고와소통(영어)"), courseMap.get("RGC1085")),
                    new GroupCourses(groupMap.get("사고와소통(글쓰기)"), courseMap.get("RGC0005")),
                    new GroupCourses(groupMap.get("사고와소통(글쓰기)"), courseMap.get("RGC1064")),
                    new GroupCourses(groupMap.get("디지털리터러시"), courseMap.get("RGC1091")),
                    new GroupCourses(groupMap.get("디지털리터러시"), courseMap.get("RGC1092")),
                    new GroupCourses(groupMap.get("디지털리터러시"), courseMap.get("RGC1095")),
                    new GroupCourses(groupMap.get("기본소양(9)"), courseMap.get("EGC7026")),
                    new GroupCourses(groupMap.get("기본소양(9)"), courseMap.get("PRI4041")),
                    new GroupCourses(groupMap.get("기본소양(9)"), courseMap.get("EGC4039")),
                    new GroupCourses(groupMap.get("기본소양(6)"), courseMap.get("EGC7026")),
                    new GroupCourses(groupMap.get("기본소양(6)"), courseMap.get("PRI4041")),
                    new GroupCourses(groupMap.get("기본소양(6)"), courseMap.get("EGC4039")),
                    new GroupCourses(groupMap.get("BSM(수학)"), courseMap.get("PRI4001")),
                    new GroupCourses(groupMap.get("BSM(수학)"), courseMap.get("PRI4023")),
                    new GroupCourses(groupMap.get("BSM(수학)"), courseMap.get("PRI4024")),
                    new GroupCourses(groupMap.get("BSM(이산수학)"), courseMap.get("PRI4027")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4002")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4013")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4003")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4014")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4004")),
                    new GroupCourses(groupMap.get("BSM(과학)"), courseMap.get("PRI4015")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC2004")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC4018")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC4019")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC2007")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("DAI****1")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("DAI****2")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC2011")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC2005")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSC4004")),
                    new GroupCourses(groupMap.get("전공필수"), courseMap.get("CSE2025")),
                    new GroupCourses(groupMap.get("전공필수(이산구조)"), courseMap.get("CSE2026"))
            ));
        }

        // --- 7. 학번별 요건 그룹 연결 데이터 로드 (중복 실행 방지) ---
        if (cohortRequirementGroupsRepository.count() == 0) {
            Map<String, RequirementGroups> groupMap = StreamSupport.stream(requirementGroupsRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(RequirementGroups::getGroupName, group -> group));

            List<CohortRequirementGroups> cohortRequirements = new ArrayList<>();
            // 20~22학번
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("동국인성")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("대학생활탐구")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("기업가정신과 리더십")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("영어")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("글쓰기")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("기본소양(9)")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("BSM(수학)")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("BSM(과학)")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("전공필수")));
            cohortRequirements.add(new CohortRequirementGroups("20~22", groupMap.get("전공필수(이산구조)")));

            // 23학번
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("동국인성")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("대학생활탐구")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("기업가정신과 리더십")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("영어")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("글쓰기")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("세미나(1)")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("기본소양(9)")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("BSM(수학)")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("BSM(이산구조)")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("BSM(과학)")));
            cohortRequirements.add(new CohortRequirementGroups("23", groupMap.get("전공필수")));

            // 24학번
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("동국인성")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("세미나(1)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("자기계발")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("사고와소통(영어)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("사고와소통(글쓰기)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("디지털리터러시")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("기본소양(9)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("BSM(수학)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("BSM(이산구조)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("BSM(과학)")));
            cohortRequirements.add(new CohortRequirementGroups("24", groupMap.get("전공필수")));

            // 25학번
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("동국인성")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("세미나(1)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("자기계발")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("사고와소통(영어)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("사고와소통(글쓰기)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("디지털리터러시")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("기본소양(6)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("BSM(수학)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("BSM(이산구조)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("BSM(과학)")));
            cohortRequirements.add(new CohortRequirementGroups("25", groupMap.get("전공필수")));

            cohortRequirementGroupsRepository.saveAll(cohortRequirements);
        }

        // --- 8. 테스트용 사용자 데이터 로드 ---
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .name("테스트유저")
                    .userId("2024123456")
                    .password("password123")
                    .email("test@example.com")
                    .build());

            userRepository.save(User.builder()
                    .name("테스트유저20")
                    .userId("2020123456")
                    .password("password123")
                    .email("test20@example.com")
                    .build());
        }

        // --- 9. 교과목 데이터 로드 (엑셀 파일에서 파싱) ---
        try {
            courseRepository.deleteAllInBatch();
            loadCoursesFromExcel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load courses from Excel file", e);
        }

        // --- 10. 선수과목 데이터 로드 ---
        if (prerequisiteCourseRepository.count() == 0) {
            Map<String, Course> courseMap = StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                    .collect(Collectors.toMap(Course::getClassId, course -> course));

            prerequisiteCourseRepository.saveAll(List.of(
                    new PrerequisiteCourse(courseMap.get("CSC2002"), courseMap.get("CSC2001")), // 심화프로그래밍 -> 기초프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC2003"), courseMap.get("CSC2001")), // 객체지향프로그래밍 -> 기초프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC2007"), courseMap.get("CSC2003")), // 자료구조 -> 객체지향프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC4021"), courseMap.get("CSC2001")), // 데이터통신입문 -> 기초프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC2006"), courseMap.get("CSC2001")), // 프로그래밍언어론 -> 기초프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC4004"), courseMap.get("CSC2001")), // 공개SW프로젝트 -> 기초프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC2008"), courseMap.get("CSC2007")), // 알고리즘 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4009"), courseMap.get("CSC2007")), // 데이터베이스 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4020"), courseMap.get("CSC2007")), // 데이터베이스설계 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4002"), courseMap.get("CSC2007")), // 컴퓨터그래픽스 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4012"), courseMap.get("CSC2007")), // 인공지능 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4011"), courseMap.get("CSC2007")), // 인간컴퓨터상호작용 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC2005"), courseMap.get("CSC2007")), // 시스템소프트웨어 -> 자료구조
                    new PrerequisiteCourse(courseMap.get("CSC4010"), courseMap.get("CSC2003")), // 소프트웨어공학 -> 객체지향프로그래밍
                    new PrerequisiteCourse(courseMap.get("CSC4021"), courseMap.get("PRI4001")), // 데이터통신입문 -> 미적분학및연습1
                    new PrerequisiteCourse(courseMap.get("CSC4021"), courseMap.get("PRI4001")), // 데이터통신입문 -> 미적분학및연습1
                    new PrerequisiteCourse(courseMap.get("CSC4022"), courseMap.get("PRI4023")), // 머신러닝 -> 확률및통계학
                    new PrerequisiteCourse(courseMap.get("CSC4022"), courseMap.get("PRI4023")), // 머신러닝 -> 확률및통계학
                    new PrerequisiteCourse(courseMap.get("CSC4015"), courseMap.get("CSC4014")), // 컴파일러 -> 형식언어
                    new PrerequisiteCourse(courseMap.get("CSC2011"), courseMap.get("PRI4027")), // 컴퓨터구성 -> 이산수학
                    new PrerequisiteCourse(courseMap.get("CSC4014"), courseMap.get("PRI4027")), // 형식언어 -> 이산수학
                    new PrerequisiteCourse(courseMap.get("CSC4013"), courseMap.get("CSC2011")), // 컴퓨터구조 -> 컴퓨터구성
                    new PrerequisiteCourse(courseMap.get("CSC4001"), courseMap.get("CSC2005")), // 운영체제 -> 시스템소프트웨어
                    new PrerequisiteCourse(courseMap.get("CSC4031"), courseMap.get("CSC2005")), // 양자컴퓨팅 -> 시스템소프트웨어
                    new PrerequisiteCourse(courseMap.get("CSC4005"), courseMap.get("CSC4001")), // 임베디드시스템 -> 운영체제
                    new PrerequisiteCourse(courseMap.get("CSC4031"), courseMap.get("PRI4024")), // 양자컴퓨팅 -> 공학선형대수학
                    new PrerequisiteCourse(courseMap.get("CSC4004"), courseMap.get("CSC2004")), // 공개SW프로젝트 -> 어드벤처디자인
                    new PrerequisiteCourse(courseMap.get("CSC4018"), courseMap.get("CSC4004")), // 종합설계1 -> 공개SW프로젝트
                    new PrerequisiteCourse(courseMap.get("CSC4019"), courseMap.get("CSC4018"))  // 종합설계2 -> 종합설계1
            ));
        }
    }

    private void loadCoursesFromExcel() throws IOException {
        ClassPathResource resource = new ClassPathResource("courses.xlsx");
        if (!resource.exists()) {
            return;
        }

        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        // 헤더 행 건너뛰기
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        List<Course> courses = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row)) {
                continue;
            }

            courses.add(Course.builder()
                    .classId(getCellValueAsString(row.getCell(0)))
                    .className(getCellValueAsString(row.getCell(1)))
                    .credits(getCellValueAsString(row.getCell(2)))
                    .theory(getCellValueAsString(row.getCell(3)))
                    .practice(getCellValueAsString(row.getCell(4)))
                    .requiredDivision(getCellValueAsString(row.getCell(5)))
                    .targetCredit(getCellValueAsString(row.getCell(6)))
                    .recommendedSemester(getCellValueAsString(row.getCell(7)))
                    .notes(getCellValueAsString(row.getCell(8)))
                    .scoreWebDev((int) getCellValueAsDouble(row.getCell(9)))
                    .scoreAppDev((int) getCellValueAsDouble(row.getCell(10)))
                    .scoreGameDev((int) getCellValueAsDouble(row.getCell(11)))
                    .scoreDataAiDev((int) getCellValueAsDouble(row.getCell(12)))
                    .scoreInfosec((int) getCellValueAsDouble(row.getCell(13)))
                    .scoreCloudDevops((int) getCellValueAsDouble(row.getCell(14)))
                    .scoreTechPm((int) getCellValueAsDouble(row.getCell(15)))
                    .scoreSpecialTech((int) getCellValueAsDouble(row.getCell(16)))
                    .build());
        }

        // '개별연구' 과목들 하드코딩
        courses.add(Course.builder().classId("DAI****1").className("개별연구1").credits("3").theory("3").practice("0").requiredDivision("전공").targetCredit("3,4").recommendedSemester("1").notes("DAI****").build());
        courses.add(Course.builder().classId("DAI****2").className("개별연구2").credits("3").theory("3").practice("0").requiredDivision("전공").targetCredit("3,4").recommendedSemester("2").notes("DAI****").build());
        courses.add(Course.builder().classId("CSE2025").className("계산적사고법").credits("3").theory("3").practice("0").requiredDivision("전공").targetCredit("2").recommendedSemester("1").notes("").build());
        courses.add(Course.builder().classId("CSE2026").className("이산구조").credits("3").theory("3").practice("0").requiredDivision("전공").targetCredit("2").recommendedSemester("1").notes("").build());

        courseRepository.saveAll(courses);
        workbook.close();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return 0.0;
        }
        return cell.getNumericCellValue();
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.hasText(cell.toString())) {
                return false;
            }
        }
        return true;
    }
}