package com.example.orderservice.grpc;

import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import com.example.orderservice.vo.ResponseOrder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void getOrders(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        Iterable<OrderEntity> orders = orderRepository.findByUserId(request.getUserId());

        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        List<com.example.orderservice.grpc.Order> grpcOrders = result.stream().map(order ->
                com.example.orderservice.grpc.Order.newBuilder()
                        .setOrderId(order.getOrderId())
                        .setProductId(order.getProductId())
                        .setQty(order.getQty())
                        .setUnitPrice(order.getUnitPrice())
                        .setTotalPrice(order.getTotalPrice())
                        .setCreatedAt(order.getCreatedAt().toString())
                        .build()
        ).toList();

        OrderResponse response = OrderResponse.newBuilder()
                .addAllOrders(grpcOrders)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
