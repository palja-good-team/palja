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

</details>

<details>
    <summary>결제 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>주문 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>회원 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>쿠폰 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>상품 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>리뷰 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

## 시스템 아키텍처
<img width="1000" height="400" alt="image" src="https://github.com/user-attachments/assets/b2597a64-d0c0-440f-bc1d-f84fb9156ad0"/>


## 애그리거트 루트 구성
<img width="600" height="300" alt="image" src="https://github.com/user-attachments/assets/c31aae78-b003-4983-b6e6-f6c58b3513e4" />


## ERD
<img width="1000" height="600" alt="image" src="https://github.com/user-attachments/assets/fb6c03df-674c-4fa7-8b0b-ab3c23f4d17f" />

## API 명세서
<details>
    <summary>타임딜 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>결제 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>주문 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>회원 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>쿠폰 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>상품 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

<details>
    <summary>리뷰 서비스</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

</details>

## 팀원 및 역할

| 이름 | 담당 업무 |  |
| --- | --- | --- |
| 김민선 | 타임딜 서비스 | [GitHub](https://github.com/alscksdlek) |
| 김혜윤 | 결제 서비스 | [GitHub](https://github.com/S2hyeyunS2) |
| 김부경 | 주문 서비스 | [GitHub](https://github.com/bu119) |
| 이건희 | 회원 서비스 및 인증/인가 | [GitHub](http://github.com/geongeongeon) |
| 이원규 | 쿠폰 서비스 | [GitHub](https://github.com/bitamin707) |
| 조재희 | 상품, 리뷰 서비스 | [GitHub](http://github.com/newbee9507) |
