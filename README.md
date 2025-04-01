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
### Sharding 
* user-service의 DB를 2개의 Mariadb(shard1, shard2)로 분리하여 저장한 다음, Sharding key를 구분하여 2개의 DB에 분산되어 저장하도록 처리
> * branch 명: async
* 초기 테이블이가 shard1(또는 shard2)에만 생길 수 있기 때문에, 다른 쪽 shard에 수동으로 테이블 생성 필요
* 전체 데이트 조회하는 API는 2개의 DB에 분산되어 저장된 데이터를 모두 가져오도록 구현되어 있지 않았기 때문에, 한쪽의 데이터만 보임