# 프로젝트 목적 및 진행 내역
매주 학습한 내용을 실습하기 위해, 이커머스 애플리케이션을 주제로 토이 프로젝트를 진행하고 적용 가능한 이슈를 팀원들과 함께 만들어 해결하고 있습니다.

## 현재 진행 내역
- [x] **Spring 기반 백엔드 개발**  
  개발자가 반드시 정복해야할 객체지향과 디자인 패턴 도서 및 JPA 강의를 학습하며 익힌 내용 적용
- [x] **DDD 기반 리팩토링**  
  DDD 시작하기 도서를 참고하여 도메인 주도 설계 중 일부 적용
- [ ] **모듈 분리 계획**  
  `order` 서비스 분리를 준비 중

<br>

# 패키지 전략 개요
각각의 하위 도메인마다 컨텍스트 패키지를 만들었습니다.

## 컨텍스트별 패키지
컨텍스트마다 반복되는 패키지 구조를 그림으로 나타냈습니다.<br>
<img width="450" height="538" alt="image" src="https://github.com/user-attachments/assets/c1d118ec-01e6-49c2-b277-a760a1042cca" />

<br>

# 사용 기술
<img width="2099" height="1146" alt="372933998-467e9f95-31c0-473f-b52b-6eacae2c1573" src="https://github.com/user-attachments/assets/b0eba229-f7fb-4cb1-bdff-2d5cfc3c73ce" />

## 데이터베이스 접근 기술
1. command 기능의 경우, 어그리거트 단위로 관리하므로 JPA의 연관관계 기능을 활용하여 만든다.
2. query 기능의 경우, 매우 복잡한 쿼리의 경우 MyBatis를 사용할려고 한다.

<br>

# 테스트 개요
테스트 및 로컬 개발 환경에서 `docker-compose`를 활용해 MySQL 등 의존 서비스를 자동으로 실행합니다. 또한, **GitHub Actions를 사용한 CI 파이프라인**이 설정되어 있습니다.

## 주요 기능 및 설정

### 1. Spring Boot + Docker Compose 통합

- `spring-boot-docker-compose` 의존성 추가  
  → 테스트 또는 로컬 실행 시 `docker-compose.yml`에 정의된 의존 컨테이너(MySQL 등)가 자동 실행됩니다.

### 2. 단위 테스트 작성 및 통합 테스트 작성
- 클래스와 메서드 단위로 **단위 테스트(Unit Test)** 작성
- 외부 인프라와의 연동을 검증하는 **통합 테스트(Integration Test)** 작성
