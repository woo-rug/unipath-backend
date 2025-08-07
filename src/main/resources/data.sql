-- 직업군 데이터 삽입
INSERT INTO career_group (name, description) VALUES ('웹 개발', '웹사이트와 웹 애플리케이션을 만드는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('앱 개발', '모바일 앱을 만드는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('게임 개발', '게임 개발에 특화된 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('데이터/AI 개발', '데이터 분석 및 인공지능 기술을 다루는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('정보보안', '시스템 및 네트워크 보안을 담당하는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('클라우드/DevOps', '클라우드 환경 구축 및 운영을 담당하는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('기술 기획/관리', '개발 프로젝트의 기획, 관리 및 품질 보증을 담당하는 직업군입니다.');
INSERT INTO career_group (name, description) VALUES ('특수 기술/기타', '블록체인, 양자컴퓨팅 등 특수 기술 분야를 다루는 직업군입니다.');

-- 직업 데이터 삽입
-- 웹 개발 (career_group_id: 1)
INSERT INTO career (name, description, career_group_id) VALUES ('프론트엔드 개발자', '사용자에게 보이는 웹 화면을 개발하고, 웹 인터페이스를 구현합니다.', 1);
INSERT INTO career (name, description, career_group_id) VALUES ('백엔드 개발자', '서버와 데이터베이스를 구축하고 관리하여 웹 서비스의 핵심 기능을 구현합니다.', 1);
INSERT INTO career (name, description, career_group_id) VALUES ('풀스택 개발자', '프론트엔드와 백엔드 개발을 모두 수행하며 웹 서비스 전체를 관리합니다.', 1);

-- 앱 개발 (career_group_id: 2)
INSERT INTO career (name, description, career_group_id) VALUES ('iOS 개발자', '애플 iOS 운영체제 기반의 모바일 애플리케이션을 개발합니다.', 2);
INSERT INTO career (name, description, career_group_id) VALUES ('Android 개발자', '구글 안드로이드 운영체제 기반의 모바일 애플리케이션을 개발합니다.', 2);
INSERT INTO career (name, description, career_group_id) VALUES ('크로스플랫폼 개발자', 'iOS와 Android에서 모두 작동하는 모바일 앱을 개발합니다.', 2);

-- 게임 개발 (career_group_id: 3)
INSERT INTO career (name, description, career_group_id) VALUES ('클라이언트 개발자', '게임의 그래픽, 사용자 인터페이스, 플레이어 로직 등 눈에 보이는 부분을 개발합니다.', 3);
INSERT INTO career (name, description, career_group_id) VALUES ('서버 개발자', '게임 서버를 구축하여 사용자 데이터와 게임 룰을 관리하는 역할을 합니다.', 3);
INSERT INTO career (name, description, career_group_id) VALUES ('게임 기획자', '게임의 컨셉, 스토리, 규칙 등을 설계하여 개발 프로세스를 이끕니다.', 3);

-- 데이터/AI 개발 (career_group_id: 4)
INSERT INTO career (name, description, career_group_id) VALUES ('데이터 분석가', '데이터를 수집, 분석하여 사업적인 인사이트를 도출하고 의사결정을 돕습니다.', 4);
INSERT INTO career (name, description, career_group_id) VALUES ('데이터 엔지니어', '방대한 데이터를 효율적으로 처리하고 저장하는 시스템을 구축하고 관리합니다.', 4);
INSERT INTO career (name, description, career_group_id) VALUES ('AI/머신러닝 개발자', '머신러닝 모델을 개발하고 AI 기반 서비스를 구축하는 역할을 합니다.', 4);

-- 정보보안 (career_group_id: 5)
INSERT INTO career (name, description, career_group_id) VALUES ('화이트 해커(보안 컨설턴트)', '시스템의 취약점을 찾아내고 보안 위협에 대한 해결책을 제시합니다.', 5);
INSERT INTO career (name, description, career_group_id) VALUES ('시스템 보안 개발자', '안전한 시스템 및 소프트웨어를 개발하고 보안 기능을 설계합니다.', 5);
INSERT INTO career (name, description, career_group_id) VALUES ('웹 보안 전문가', '웹사이트와 웹 애플리케이션의 보안 취약점을 진단하고 방어하는 전문가입니다.', 5);

-- 클라우드/DevOps (career_group_id: 6)
INSERT INTO career (name, description, career_group_id) VALUES ('DevOps 엔지니어', '개발(Dev)과 운영(Ops)의 협업을 촉진하고 자동화하여 효율성을 높입니다.', 6);
INSERT INTO career (name, description, career_group_id) VALUES ('클라우드 엔지니어', 'AWS, Azure, GCP 등 클라우드 플랫폼을 활용하여 인프라를 구축하고 운영합니다.', 6);
INSERT INTO career (name, description, career_group_id) VALUES ('인프라 엔지니어', '서버, 네트워크 등 시스템의 기반 환경을 구축하고 안정적으로 관리합니다.', 6);

-- 기술 기획/관리 (career_group_id: 7)
INSERT INTO career (name, description, career_group_id) VALUES ('기술 PM', '기술적인 지식을 바탕으로 프로젝트의 일정, 자원, 팀원을 관리하고 목표를 달성합니다.', 7);
INSERT INTO career (name, description, career_group_id) VALUES ('정보보안 관리자', '기업의 정보보안 정책을 수립하고 보안 시스템을 관리하는 역할을 합니다.', 7);
INSERT INTO career (name, description, career_group_id) VALUES ('QA 엔지니어', '제품의 품질을 보증하기 위해 다양한 테스트를 계획하고 수행하며 품질 개선을 주도합니다.', 7);

-- 특수 기술/기타 (career_group_id: 8)
INSERT INTO career (name, description, career_group_id) VALUES ('블록체인 개발자', '분산원장기술(DLT)을 활용하여 암호화폐, 스마트 컨트랙트 등을 개발합니다.', 8);
INSERT INTO career (name, description, career_group_id) VALUES ('양자컴퓨팅 연구원', '양자역학 원리를 이용한 새로운 컴퓨팅 기술을 연구하고 개발합니다.', 8);
INSERT INTO career (name, description, career_group_id) VALUES ('개발 문서 전문가', '기술 문서를 작성하고 관리하여 개발자와 사용자 간의 소통을 돕습니다.', 8);

-- 질문 데이터 삽입
INSERT INTO question (question) VALUES ('나는 복잡한 문제를 해결하기 위해 논리적으로 접근하는 것을 즐긴다.'); -- 1
INSERT INTO question (question) VALUES ('새로운 기술이나 이론을 배우는 것에 대해 강한 호기심을 느낀다.'); -- 2
INSERT INTO question (question) VALUES ('다른 사람들과 함께 목표를 달성하기 위해 협력하는 것을 선호한다.'); -- 3
INSERT INTO question (question) VALUES ('보이지 않는 시스템의 내부 구조를 설계하고 최적화하는 것에 흥미가 있다.'); -- 4
INSERT INTO question (question) VALUES ('깔끔하고 사용하기 쉬운 사용자 인터페이스를 만드는 것에 관심이 많다.'); -- 5
INSERT INTO question (question) VALUES ('사소한 오류나 버그를 찾아내고 수정하는 일에 끈기가 있다.'); -- 6
INSERT INTO question (question) VALUES ('새로운 아이디어를 내고, 이를 실제 결과물로 만드는 것을 좋아한다.'); -- 7
INSERT INTO question (question) VALUES ('데이터 속에서 의미 있는 패턴이나 규칙을 찾아내는 것에 재미를 느낀다.'); -- 8
INSERT INTO question (question) VALUES ('다양한 사람들의 의견을 듣고 종합하여 더 나은 방향을 제시하는 편이다.'); -- 9
INSERT INTO question (question) VALUES ('최신 트렌드를 빠르게 파악하고, 이를 적용하는 것을 좋아한다.'); -- 10

-- 질문-직업군 가중치 데이터 삽입
-- Q1: 논리적 접근
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (1, 1, 2); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (1, 3, 1); -- 게임 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (1, 4, 3); -- 데이터/AI 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (1, 5, 3); -- 정보보안
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (1, 6, 2); -- 클라우드/DevOps

-- Q2: 새로운 기술/이론에 대한 호기심
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (2, 2, 1); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (2, 3, 2); -- 게임 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (2, 4, 2); -- 데이터/AI 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (2, 8, 3); -- 특수 기술/기타

-- Q3: 협력 선호
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (3, 1, 1); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (3, 3, 2); -- 게임 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (3, 6, 3); -- 클라우드/DevOps
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (3, 7, 3); -- 기술 기획/관리

-- Q4: 시스템 내부 구조/최적화
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (4, 1, 3); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (4, 2, 2); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (4, 5, 1); -- 정보보안
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (4, 6, 2); -- 클라우드/DevOps

-- Q5: 깔끔한 UI/UX 관심
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (5, 1, 3); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (5, 2, 3); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (5, 3, 2); -- 게임 개발

-- Q6: 버그 수정에 대한 끈기
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (6, 2, 1); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (6, 4, 2); -- 데이터/AI 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (6, 5, 3); -- 정보보안
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (6, 7, 2); -- 기술 기획/관리

-- Q7: 새로운 아이디어 구현
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (7, 1, 2); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (7, 2, 2); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (7, 3, 3); -- 게임 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (7, 8, 2); -- 특수 기술/기타

-- Q8: 데이터 속 패턴 찾기
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (8, 4, 3); -- 데이터/AI 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (8, 5, 2); -- 정보보안

-- Q9: 다양한 의견 종합/방향 제시
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (9, 3, 1); -- 게임 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (9, 6, 2); -- 클라우드/DevOps
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (9, 7, 3); -- 기술 기획/관리

-- Q10: 최신 트렌드 파악/적용
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (10, 1, 2); -- 웹 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (10, 2, 2); -- 앱 개발
INSERT INTO question_weight (question_id, career_group_id, weight) VALUES (10, 8, 2); -- 특수 기술/기타
