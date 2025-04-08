### MSA Communications Samples
* user-esrvice와 order-service 간의 통신에 대한 샘플 코드
* user-service에서 사용자 ID에 대한 상세 정보 조회 시 order-service로부터 사용자가 주문한 주문 목록을 가져오기
> * restful API 사용
    >   * branch 명: main
> * gRPC API 사용
    >   * branch 명: grpc
> * graphql API 사용
    >   * branch 명: graphql

### API Gateway & Service Discovery / Registry
* apigateway-service를 사용하는 user-esrvice와 order-service 간의 통신에 대한 샘플 코드
> * branch 명: apigateway
* user-service와 order-service를 사용하기 위해 apigateway-service를 사용하는 예제
> * user-service와 order-service는 service-discovery(eureka)에 등록
> * service-discovery에 등록 된 서비스들에 대한 정보를 주기적으로 확인
* 단순한 Proxy 정보는 apigateway-service의 application.yml의 routes에 등록 된 정보 사용
* 복잡한 처리를 위해서는 apigateway-service에서 별도의 처리 Controller(BFF) 사용

### Asynchronous communications
* Kafka Broker를 사용하여 order-service와 shipping-service 사이의 데이터 동기화 처리
> * branch 명: async
* user-service에서 주문을 요청하면 order-service에서 주문 데이터를 저장한 다음, 해당 주문 데이터를 Kafka Topic으로 전송
* shipping-service에서는 Kafka Topic에 전달 된 메시지를 가지고 배송 데이터로 저장

### SAGA
* 분산 트랜잭션에서 오류 발생 시 처리 작업을 이전으로 돌리기 위한(Roll back) 보상 트랜잭션 처리
> * branch 명: saga1
* user-service에서 주문을 요청하면 order-service에서 주문 데이터를 저장
* order-service에서는 주문 데이터를 Kafka Topic으로 전송
* payment-service에서는 Kafka Topic에 전달 된 메시지를 가지고 결제 처리 프로세스 시작, 결제 결과를 Kafka Topic으로 전송
    * 결제가 실패 되었을 때는 Kafka Topic에 실패 메시지를 전달
* shipping-service에서
    * 결제가 성공 되었을 때, "배송 시작" 상태로 변경
    * 결제가 실패 되었을 때, Kafka Topic에 실패 메시지를 전달하고 상태를 "실패"로 변경

### EDA
* 서비스간 통신에서 API를 직접 호출하는 것이 아닌, 이벤트 발생/구독을 통해 처리하는 Event-Driven Architecture 사용
> * branch 명: eda
* JSON Schema 검증 방식을 이용하여 Kafka에 전송되는 메시지에 대해 버전 관리 및 검증 처리
* 기존 KafkaTemplate<String, Object>를 KafkaTemplate<String, String>로 변경
* Kafka에 메시지를 발송/수신할 떄 검증 처리 - order-created.schema.json

### Resilience
* 서비스 간 통신에 오류가 발생하는 경우 장애 격리를 위한 작업
> * branch 명: resilience
* 주문정보가 포함된 사용자 상세보기 요청을 위해, user-service에서 order-service를 호출하게 되는데, order-service에 문제가 발생할 경우에 대한 장애 격리 처리
    * retry, circuit breaker, bulk head, timeout, fallback 처리
    * service 메소드 내에서 다른 메소드를 호출하는 경우에는 Annotation으로 선언한 resilience 작업이 실행되지 않으며, 이를 처리하기 위한 별도의 클래스를 생성하여 작업 처리하도록 구현 (user-service 프로젝트의 OrderService.java)

### Observability
* 서비스 간에 발생되는 로그를 Fluend 서비스에 통합하여 처리
> * branch 명: observability
* user-service, order-service, payment-service, shipping-service의 로그를 fluentd-central에 저장
* fluentd-central에서 마이크로서비스의 모든 로그를 관리
* zipkin을 이용하여 정보 수집

### Security
* 서비스 API 호출에 제한을 두기 위한 Rate Limiting 처리
> * branch 명: security
* user-service의 Filter에 1분에 10회 이상의 호출이 되지 않도록 수정,
* user-service의 health-check API가 10초안에 5회 이상 호출 되지 않도록 수정

### Test
* 마이크로서비스에 대한 테스트 전략 
> * branch 명: test
* user-service API에 대한 테스트 코드
  * UserServiceImplUnitTest
  * UserServiceComponentTest
  * UserServiceIntegrationTest
  * UserServiceE2ETest

### Cache
* 마이크로서비스에 캐시 처리 추가
> * branch 명: cache
* user-service에서 로그인 시 사용자 ID 정보를 Redis저장 
* order-service에서 사용자 주문목록 확인 시, 2번째 부터는 캐시에 기록된 정보를 이용하여 반환
  * 주문 추가시 사용자 세션 삭제 로직 필요