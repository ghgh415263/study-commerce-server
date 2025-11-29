# 프로젝트 목적 및 진행 내역
학습한 내용을 실습하기 위해, 이커머스 애플리케이션을 주제로 토이 프로젝트를 진행하고 적용 가능한 이슈를 팀원과 함께 만들어 해결하고 있습니다.

### 현재 진행 내역
- [x] **Spring 기반 백엔드 개발**  
  - 개발자가 반드시 정복해야할 객체지향과 디자인 패턴 도서 학습하여 적용
  - 스프링과 JPA 강의를 학습하며 익힌 내용 적용
- [x] **DDD 기반 리팩토링**  
  - DDD 시작하기 도서를 참고하여 도메인 주도 설계 중 일부 적용
  - 구현의 편의성을 위해서 @Entity와 JPA 연관 관계를 통해서 어그리거트 구현
  - 각 컨텍스트별로 모델 분리. 현재는 member, order, product 컨텍스트를 유지중
- [ ] **모듈 분리 계획**  
  `order` 서비스 분리를 준비 중 (HTTP 호출과 카프카를 활용한 비동기 호출도 사용할 예정)

<br>

# 패키지 전략 개요
각각의 하위 도메인마다 컨텍스트 패키지를 만들었습니다.

### 컨텍스트별 패키지
컨텍스트마다 반복되는 패키지 구조를 그림으로 나타냈습니다.<br>
<img width="450" height="538" alt="image" src="https://github.com/user-attachments/assets/c1d118ec-01e6-49c2-b277-a760a1042cca" />


1. UI Layer
    - 사용자가 시스템에 어떻게 접근하고 데이터를 주고받는지 담당
    - Controller, DTO, 단순 Validation 등 API/화면 입출력 처리

2. Application Layer
    - 도메인을 어떤 흐름으로 사용할지 정의하는 유스케이스 계층
    - 트랜잭션 경계 설정, 도메인 객체 조합 및 orchestration

3. Domain Layer
    - 비즈니스 규칙의 핵심 (Entity, Value Object, Domain Service, Aggregate)
    - 기술 의존 최소화, 순수한 자바 기반 구현 (구현의 편의성으로 jpa 관련 코드는 약간 허용)

4. Infra Layer
    - DB 접근, 메시지 브로커, 외부 API 등 기술 요소 구현
    - 기술 변경이 이 레이어에 집중되도록 분리

5. DAO Layer (Query/Read Model)
    - 도메인 규칙이 거의 필요 없는 조회 전용 계층
    - 화면/리스트/검색 등 UI 요구에 맞춘 데이터 조회
    - Domain/Application을 통과하지 않고 UI → DAO → Infra 구조로 처리

<br>

# 사용 기술
<img width="2099" height="1146" alt="372933998-467e9f95-31c0-473f-b52b-6eacae2c1573" src="https://github.com/user-attachments/assets/c19ac04e-0da4-4ddc-891e-3ad7484b42aa" />

### 데이터베이스 접근 기술
1. command 기능의 경우, 어그리거트 단위로 관리하므로 JPA의 엔티티와 연관관계 기능을 활용하여 만든다.
   - 라이프사이클이 같은 엔티티끼리는 묶는다.
   - 예를들어, 상품과 상품 태그는 하나의 어그리거트로 묶고 상품과 상품 리뷰는 다른 어그리거트이다.
3. query 기능의 경우, 화면에 뿌려줄 데이터를 가져오는 기능들을 말한다.
   - 인터페이스를 놓고 구현체들은 JPA, Mybatis, JdbcTemplate 등 해당 기능을 구현하는데 쉬운 방법을 사용할 것이다.
  
### 메시지 브로커 사용 기술
1. 스프링 카프카를 활용하여 브로커와 통신
2. 아웃박스 패턴과 멱등 컨슈머를 활용함

### 인증 기술
1. 단일 서버임으로 session 사용하였음. 추후 서비스 분리를 위해서 self-contained token 인증 방식으로 바꿈
2. 인증이 필요한 컨트롤러 호출의 경우, 인터셉터에서 토큰을 검증한다.

### 테스트 환경을 위한 기술
1. 도커 컴포즈를 활용하여 로컬 테스트시 필요한 인프라 기동할 수 있게 만듬
   - Mysql
   - Prometheus, Grafana
   - Kafka, Kafka-ui
<br>

# 테스트 개요
테스트 및 로컬 개발 환경에서 `docker-compose`를 활용해 MySQL 등 의존 서비스를 자동으로 실행합니다. 또한, **GitHub Actions를 사용한 CI 파이프라인**이 설정되어 있습니다.

### 주요 기능 및 설정

#### 1. Spring Boot + Docker Compose 통합

- `spring-boot-docker-compose` 의존성 추가  
  → 테스트 또는 로컬 실행 시 `docker-compose.yml`에 정의된 의존 컨테이너(MySQL 등)가 자동 실행됩니다.

#### 2. 단위 테스트 작성 및 통합 테스트 작성
- 클래스와 메서드 단위로 **단위 테스트(Unit Test)** 작성
- 외부 인프라와의 연동을 검증하는 **통합 테스트(Integration Test)** 작성

<br>

# 컨텍스트별 물리적 ERD
현재는 회원, 주문, 상품 컨텍스트를 유지중

### Member Context
### Order Context
<img width="1697" height="1049" alt="image" src="https://github.com/user-attachments/assets/d2ee5a00-eaa2-4dff-a475-520a4b350b2f" />

### Product Context
<img width="961" height="435" alt="505640443-359189eb-6044-4464-86dc-02c9c5ddd4eb" src="https://github.com/user-attachments/assets/60c8cd9f-d758-420f-ae9b-801ebfb0d492" />
