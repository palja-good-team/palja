## 프로젝트 소개

**대용량 트래픽을 고려한 마이크로서비스 기반 타임딜 이커머스 플랫폼**

타임딜과 선착순 쿠폰을 안정적으로 제공하는 이커머스 플랫폼입니다.<br>
마이크로서비스 아키텍처(MSA)를 기반으로 설계되어 각 서비스 독립적으로 동작하며,
유연성과 확장성을 확보하면서 안정적인 운영을 목표로 합니다.

## 프로젝트 목표

1. 비즈니스 목표
    - **대용량 트래픽 처리** : 타임딜 주문 동시 요청 1,000명 이상 안정적으로 처리
    - **정확한 재고 관리** : 분산 환경에서 재고 수량 정합성 보장
    - **실시간 쿠폰 관리** : 발급/사용/만료 처리를 실시간으로 처리하는 이벤트 기반 시스템 구축
2. 기술적 목표
    - **MSA 아키텍처 구현** : 쿠폰, 주문, 결제 등 도메인별 서비스 분리 및 독립 배포
    - **분산 시스템 동시성 제어** : Redis 분산 락을 활용한 동시성 문제 해결
    - **이벤트 기반 비동기 처리** : Kafka를 통한 서비스 간 느슨한 결합 및 확장 가능한 구조
    - **데이터 정합성 보장** : 분산 트랜잭션 및 보상 트랜잭션 패턴 적용
3. 협업 목표
    - **도메인 주도 설계**: DDD원칙에 따른 명확한 계층 분리 및 도메인 모델 설계
    - **테스트 커버리지**: 핵심 서비스 로직 단위 테스트 작성
    - **API 문서화** : Swagger를 통한 API 명세 자동화

## 기술 스택

| 분류 | 상세 |
| ----- | ----- |
| Language & Framework | Java 17, Spring Boot 3.5.8 |
| Security | Spring Security, JWT |
| Microservices | Spring Cloud Gateway, Eureka, Spring Cloud Config |
| Messaging | Kafka |
| Cache | Redis, Redisson |
| RDBMS | PostgreSQL |
| Infrastructure  | AWS EC2, AWS RDS, Docker |
| Monitoring | Grafana, Zipkin |
| Test | JUnit5, Ngrinder |
| Tools | IntelliJ, Git, GitHub, Notion, Slack |

## 주요 기능
<details>
    <summary>타임딜 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
- 타임딜 생성: 관리자/업체 판매자 권한 확인 후 판매 기간(시작/종료), 할인 정책(할인가), 총 수량을 받아 PENDING 상태로 타임딜을 생성합니다. 이후 상품 서비스에 재고 차감 이벤트를 발행하며,
재고 차감 실패 이벤트를 구독할 경우 보상 트랜잭션을 통해 해당 타임딜을 FAILED 상태로 전환합니다.
- 타임딜 단건/목록 조회: 타임딜 아이디로 타임딜을 단건 조회, 페이징 기반으로 타임딜을 목록 조회합니다.
- 타임딜 수정: 관리자/업체 판매자 권한 확인 후 타임딜 상태에 따라 변경 가능한 필드와 불가능한 필드를 분리해 타임딜을 수정합니다.
- 타임딜 상태 변경 + 히스토리 저장: 타임딜 상태 전이 규칙을 두고 유효하지 않은 전이는 차단합니다. 상태 변경 시 TimeDealStatusHistory를 저장하여 운영 추적성을 확보합니다.
- 타임딜 상태 스케줄러로 자동 변경: 타임딜 시작/종료 시간에 맞춰 상태를 자동 변경합니다.
- 타임딜 삭제: PENDING 상태 타임딜을 논리적 삭제합니다.
- 업체 판매자 관련 타임딜 삭제 (내부용 API): 업체 판매자가 삭제되었을 때 관련된 PENDING 상태의 타임딜들만 논리 삭제합니다.
- 타임딜 재고 차감/복구(내부용 API): 주문 생성 시점에 호출되는 핵심 구간으로 Kafka 이벤트 기반 비동기 처리로 주문 시점 재고 차감·복구의 동시성과 정합성을 보장합니다.
- 상품 가격 변경 이벤트 구독: 타임딜 화면에 필요한 상품 원래 가격 등의 비정규화 필드는 상품 서비스의 변경 이벤를 구독하여 동기화합니다.

</details>

<details>
    <summary>결제 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
1. 결제 생성 (PENDING)
- 주문 서비스로부터 전달받은 주문 정보를 기반으로 결제 생성
- 동일 주문에 대해 중복 결제가 생성되지 않도록 orderId 기준으로 멱등 처리

결제 완료 (Toss 승인/실패 처리)

- Toss로 부터 전달 받은 paymentKey를 통해 결제 완료
- Toss 결제 상태가 DONE인지 검증하고, 승인 금액과 정확하게 일치하는지 검증 후 결제 상태를 APPROVED로 변경
- 검증 실패 또는 PG 응답 오류 시 결제 상태를 FAILED로 변경하고 결제 흐름을 차단

결제 취소 (Toss 결제 취소)

- 결제 상태가 승인 완료인 결제에 대해서만 취소 허용
- 전액 환불 정책을 적용하여 부분 환불 요청은 차단
- Toss 결제 취소 성공 시 결제 상태를 CANCELD로 변경하고 취소 사유를 기록
- Toss 결제 취소 실패 시 결제 상태는 APPROVED로 유지하고 실패 로그를 남겨 정합성 보장

결제 단건 조회 및 목록 조회

- 결제 ID를 기준으로 결제 정보를 단 건 조회
- 결제 ID, 결제 상태, 주문ID, 결제 시각, 페이징을 조건으로 목록 조회
- QueryDSL을 활용하여 조건에 따라 동적으로 조회 쿼리를 생성
- 사용자 권한에 따라 조회 가능 범위를 제한
    - MANAGER: 모든 결제 조회 가능
    - CUSTOMER: 본인 결제만 조회 가능

결제 로그 관리 (관리자)

- Toss 결제에 대한 모든 결과를 PaymentLog로 기록
- Toss 응답 코드 및 메시지를 함꼐 저장하여 장애 분석 및 CS 대응이 가능하도록 설계

결제 로그 삭제

- 결제 데이터의 무한 증가로 인한 성능 저하를 방지하기 위해 결제 완료 후 1년이 지난 데이터는 스케줄러를 통해 자동 삭제
- 스케줄러는 매일 자정에 실행되어 삭제 대상 데이터를 일괄 정리해 불필요한 로그 데이터 누적을 방지

Outbox 기반 결제 이벤트 발행

- 결제에 대한 이벤트를 Outbox 테이블에 이벤트를 먼저 적재 후 이벤트 유실 방지
- Poller를 통해 PENDING 상태의 이벤트를 순차적으로 Kafka로 발행
- 이벤트 발행 성공시 Outbox 상태를 SENT로 변경
- 이벤트 발행 실패시 Outbox 상태를 FAILED로 변경하고 retryCount 증가 및 errorMessage 저장

결제 재시도 제어

- 결제 완료 및 취소 요청이 PG사 문제로 인해 실패, 네트워크 오류로 실패할 경우 Redis에 실패 횟수 기록
- TTL 5분 동안 실패 횟수가 3회 이상이면 해당 결제에 대해 추가 요청을 차단

</details>

<details>
<summary>주문 관리</summary>

<details>
<summary>주문 생성 (Saga 패턴)</summary>

- 주문 생성 요청을 받아 Order 엔티티와 OrderSaga를 생성하고, 트랜잭션 커밋 후 Saga 시작 이벤트를 Kafka로 발행합니다. Saga Orchestrator는 재고 예약 → 쿠폰 적용 → 결제 생성 단계를 순차적으로 진행하며, 각 단계의 성공/실패 응답을 Kafka로 수신하여 다음 단계로 진행하거나 보상 트랜잭션을 실행합니다.

    **Saga Step 구성**
    - **STOCK_RESERVED**: 재고 서비스에 재고 차감 요청 이벤트 발행
    - **COUPON_APPLIED**: 쿠폰 서비스에 쿠폰 사용 요청 이벤트 발행 (쿠폰 없을 시 건너뛰기)
    - **PAYMENT_CREATED**: 결제 서비스에 결제 생성 요청 이벤트 발행

    **멱등성 보장**
    - OrderSaga의 currentStep과 isTerminated() 상태를 DB에서 체크하여 중복 처리 방지
    - Kafka Consumer에서 동일 sagaId에 대한 중복 메시지 수신 시 즉시 스킵

    **보상 트랜잭션**
    - 실패 시점의 currentStep부터 역순으로 보상 실행 (Best Effort)
    - 재고 복구, 쿠폰 취소, 결제 취소 이벤트를 순차적으로 발행
    - 보상 실패 시에도 다음 보상을 계속 진행하며 실패 로그 기록

    **DLT 기반 재시도 전략**
    - Kafka Consumer에서 메시지 처리 실패 시 2초 간격으로 3번 재시도
    - 3번 재시도 후에도 실패하면 `{topic}.DLT`로 이동
    - DLT 메시지는 별도 모니터링 Consumer에서 감지하여 로깅

    **쿠폰 스킵 처리**
    - OrderSaga의 상태 전이 규칙에서 `STOCK_RESERVED → PAYMENT_CREATED` 직접 전이 허용
    - Orchestrator의 `continueAfterStep()`에서 다음 Step이 COUPON_APPLIED인데 쿠폰이 없으면 자동으로 PAYMENT_CREATED Step으로 건너뛰기

</details>

<details>
<summary>주문 취소</summary>

- 주문 상태별 취소 가능 여부를 도메인 모델에서 검증하고, 취소 시 재고 복구 이벤트를 발행합니다. 결제 완료 상태인 경우 결제 서비스에 결제 취소 이벤트를 추가로 발행합니다.

    **취소 가능 범위**
    - **PREPARING**: 배송 준비 중 상태까지만 취소 가능
    - 배송이 시작된 후에는 취소 불가능

    **주문 상태 플로우**
    ```
    CREATED → PAID → PREPARING → SHIPPED → DELIVERED → COMPLETED
       │         │         │
       └─────────┴─────────┴────→ CANCELED  (단, SHIPPED 이전까지만)
    ```

    **배송 상태 플로우**
    ```
    READY → REQUESTED → IN_TRANSIT → DELIVERED
       ↑ 취소 가능 ↑       ↑ 취소 불가능 ↑
    ```

    **보상 처리**
    - **재고 복구**: 재고 서비스에 복구 요청 이벤트 발행
    - **쿠폰 복원**: 쿠폰 서비스에 취소 요청 이벤트 발행
    - **결제 취소**: 결제 서비스에 취소 요청 이벤트 발행

</details>

주문 단건 조회
- 사용자 역할에 따라 주문 상세 정보를 조회합니다.

    **권한별 조회 범위**
    - **주문자(CUSTOMER)**: 본인이 생성한 주문만 조회
    - **판매자(COMPANY_USER)**: 본인이 판매한 상품이 포함된 주문만 조회
    - **관리자(MANAGER, MASTER)**: 모든 주문 조회

주문 목록 조회
    - 관리자/주문자/판매자 권한에 따라 주문 목록을 조회합니다.
    - 페이징 처리를 통한 대량 데이터 조회 성능 개선

주문 상태 변경
    - 관리자가 예외 상황에서 주문 상태를 변경합니다.

주문 확정
- 배송 완료 후 7일이 경과하면 자동으로 구매 확정 처리되며, 구매자가 직접 확정할 수도 있습니다.

    **자동 확정 스케줄러**
    - 매일 새벽 3시에 실행
    - DELIVERED 상태에서 7일 경과한 주문을 `COMPLETED`로 변경

    **수동 확정**
    - 구매자가 배송 완료 후 언제든지 직접 확정 가능
    - 확정 시 주문 상태를 `COMPLETED`로 변경

결제 완료 처리
- 결제 서비스로부터 결제 완료 이벤트를 수신하여 주문 상태를 업데이트합니다.

    **멱등성 보장**
    - paymentId 기준으로 중복 처리 방지
    - 이미 처리된 결제 완료 요청은 즉시 스킵

    **상태 업데이트**
    - 주문 상태를 PAID로 변경

주문 단건 조회주문 배송 관리

송장 등록
- 판매자가 송장 번호를 등록하고 배송을 시작합니다.
    - 관리자, 판매자만 배송 정보 등록 가능 (권한 검증)
    - 송장 번호와 택배사 정보를 등록하고 주문 상태를 PREPARING로 변경

    배송 상태 변경
    배송 추적 서비스가 배송 상태를 업데이트합니다.
    - `READY → REQUESTED → IN_TRANSIT → DELIVERED` 순서로 상태 전이

배송 정보 조회
- 배송 정보를 조회합니다.

배송 수정
- 배송 정보(수령인 정보, 배송 메시지)를 수정합니다.
    - READY 상태에서만 가능합니다.

    **배송 상태별 작업 가능 여부**

    | 상태 | 배송정보 수정 | 주문 취소 |
    |------|--------------|-----------|
    | READY (출고 전) | ✅ 가능 | ✅ 가능 |
    | REQUESTED (송장 발행) | ❌ 불가 | ✅ 가능 |
    | IN_TRANSIT (집하됨) | ❌ 불가 | ❌ 불가 |
    | DELIVERED | ❌ 불가 | ❌ 불가 |

</details>

<details>
    <summary>회원 서비스</summary>

인증

- 로그인 : 아이디/비밀번호를 검증하고 대기열에 등록 후 QueueToken 발급
- 대기열 확인 : 자신의 대기 순서를 확인하고 Redis 화이트 리스트에 QueueToken 등록
- 토큰 발급 : 화이트 리스트에 등록된 QueueToken일 경우 AccessToken, RefreshToken 발급
- 토큰 재발급 : 화이트 리스트에 등록된 RefreshToken일 경우 화이트 리스트에 새로운 RefreshToken을 등록하고 AccessToken 발급
- 로그아웃 : AccessToken을 Redis 블랙 리스트에 등록하고 RefreshToken을 화이트 리스트에서 삭제

회원

- 회원 생성
    - Master : DB 쿼리를 통해 생성
    - Manager : Master가 생성
    - Customer : 누구나 생성
    - CompanyUser : 누구나 생성할 수 있지만 Manager의 승인 후 계정 활성화
- 회원 조회 : 회원 목록 조회 및 상세 조회
- 회원 수정 : 회원 정보 일부 수정
- 회원 탈퇴
    - 관리자에 의한 탈퇴 : 회원 Soft Delete 처리
    - 본인에 의한 탈퇴 : Soft Delete 처리 후 회원 삭제 이벤트를 발행하여 AccessToken을 블랙 리스트에 저장하고 RefreshToken을 화이트 리스트에서 삭제

</details>

<details>
    <summary>쿠폰 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
쿠폰 관리 (Manager)

- 쿠폰 생성 : 할인 타입, 발급 기간, 수량 등을 설정하여 쿠폰 생성
- 쿠폰 수정 : 발급되기 전 쿠폰의 정보 수정
- 쿠폰 상태 관리 : ACTIVE, PAUSED, EXPIRED, DELETED 상태 전환
- 쿠폰 조회 : 쿠폰 목록 및 상세 정보 조회

쿠폰 발급 (User)

- 선착순 쿠폰 발급 : Redis 분산 락과 캐싱을 활용하여 쿠폰 발급
    - Redisson을 이용한 분산 락 구현
    - Redis 기반 실시간 쿠폰 재고 관리
    - DB 저장 실패 시 Redis 롤백 처리
- 쿠폰 사용 : 주문 서비스로부터 Kafka 이벤트 수신하여 사용 처리
- 쿠폰 취소 : 주문 서비스로부터 Kafka 이벤트 수신하여 취소 처리
- 쿠폰 조회 : 사용자의 보유 쿠폰 목록 및 상세 정보 조회
- 쿠폰 상태 관리 : ISSUED, USED, EXPIRED 상태 전환
- 쿠폰 삭제 : 미사용된 쿠폰 소프트 삭제

</details>

<details>
    <summary>상품 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
상품 생성

- 이름, 설명, 가격, 재고, 카테고리를 입력해 생성한다
- 이름과 카테고리, 유저의 아이디를 합쳐서 중복되지 않아야 한다.

상품 수정

- 이름, 설명, 가격, 카테고리를 수정한다
- 일부 입력값만 들어오면, 프론트에서 지난 값들을 넣어준다는 시나리오로 진행

상품 재고

- 상품을 생성한 유저와 같은 유저가 상품의 재고를 조정할 수 있다
- 타임딜이나 상품의 재고에서 차감된다
    - 재고가 부족하다면 타임딜에게 메세지를 전달하고 작업을 취소한다
    - 재고 차감에 성공한다면, DB와 레디스에 재고의 변동을 적용한다
- 주문 생성시, 상품의 재고가 차감된다
    - 재고가 부족하다면 주문에게 메세지를 전달하고 작업을 취소한다
    - 재고 차감에 성공한다면, 레디스에만 재고의 변동을 적용한다.
    - 이후 스케줄러를 통해 레디스의 변경사항을 DB에 적용한다.

상품 삭제

- 상품의 아이디를 통해 DB에서 상품을 삭제한다
    - 상품을 생성한 유저의 요청만 성공한다
    - 상품의 삭제는 논리적 삭제로 진행된다
    - 상품이 삭제될때, 해당 상품의 재고정보가 레디스에 있다면 이 역시 삭제된다

</details>

<details>
    <summary>게이트웨이 & 디스커버리 서버</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
게이트웨이

- 헤더에 AccessToken이 존재하는지 확인 후 검증
- 유효하는 AccessToken일 경우 Claim에서 LoginID와 Role을 꺼내 헤더 설정

디스커버리 서버

- 각 마이크로서비스는 기동 시 Discovery Server에 자신을 등록
- Gateway 및 내부 서비스 통신(Feign)은 서비스 이름 기반으로 대상 인스턴스를 조회
- 인스턴스 증설/축소 시에도 코드 변경 없이 유연하게 대응 가능

</details>

## 시스템 아키텍처
<img width="1000" height="400" alt="image" src="https://github.com/user-attachments/assets/b2597a64-d0c0-440f-bc1d-f84fb9156ad0"/>


## 애그리거트 루트 구성
<img width="600" height="300" alt="image" src="https://github.com/user-attachments/assets/2b88b5c6-cffc-44fc-8576-df1a1ca3ecac" />

## ERD
<img width="1000" height="600" alt="image" src="https://github.com/user-attachments/assets/fb6c03df-674c-4fa7-8b0b-ab3c23f4d17f" />

## 팀원 및 역할

| 이름 | 담당 업무 |  |
| --- | --- | --- |
| 김민선 | 타임딜 서비스 | [GitHub](https://github.com/alscksdlek) |
| 김혜윤 | 결제 서비스 | [GitHub](https://github.com/S2hyeyunS2) |
| 김부경 | 주문 서비스 | [GitHub](https://github.com/bu119) |
| 이건희 | 회원 서비스 및 인증/인가 | [GitHub](http://github.com/geongeongeon) |
| 이원규 | 쿠폰 서비스 | [GitHub](https://github.com/bitamin707) |
| 조재희 | 상품, 리뷰 서비스 | [GitHub](http://github.com/newbee9507) |
