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
* Kafka Broker를 사용하여 order-service와 delivery-service 사이의 데이터 동기화 처리
> * branch 명: async
* user-service에서 주문을 요청하면 order-service에서 주문 데이터를 저장한 다음, 해당 주문 데이터를 Kafka Topic으로 전송
* delivery-service에서는 Kafka Topic에 전달 된 메시지를 가지고 배송 데이터로 저장

### CQRS + Event Sourcing
* CQRS를 이용하여 Command 작업과 Query 작업을 분리하고, 데이터 상태 변경을 Event Store(DB)에 저장
> * branch 명: cqrs1
* user-service에서 주문을 요청하면 order-service에서 주문 데이터를 저장할 때, 기존의 Orders 테이블에 저장하는 것 대신, Order_event 테이블에 상태를 기록
* 주문 내용에 대해 추가 및 수정 작업에 대해 Event sourcing 작업 처리 
* 사용자 상세정보 요청 시, 주문목록을 표시할 때 Event Store에 기록 된 상태를 Replay하여 데이터의 최종값 결정
* > * branch 명: cqrs2
* order-service에서 주문 데이터를 저장한 다음, ApplicationEventPublisher를 이용한 OrderEvent를 발행
* OrderEvent는 @EventListener로 등록 된 서비스가 Consumer로써 메시지를 소비할 수 있도록 처리