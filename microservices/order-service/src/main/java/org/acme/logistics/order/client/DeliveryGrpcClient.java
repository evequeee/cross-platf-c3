package org.acme.logistics.order.client;

import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.logistics.delivery.grpc.*;

@ApplicationScoped
public class DeliveryGrpcClient {

    @GrpcClient("delivery")
    DeliveryGrpcServiceGrpc.DeliveryGrpcServiceBlockingStub deliveryGrpc;

    public DeliveryResponse createDelivery(Long orderId, String pickupAddress, String deliveryAddress) {
        CreateDeliveryRequest request = CreateDeliveryRequest.newBuilder()
                .setOrderId(orderId)
                .setPickupAddress(pickupAddress)
                .setDeliveryAddress(deliveryAddress)
                .build();
        return deliveryGrpc.createDelivery(request);
    }

    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        GetDeliveryByOrderRequest request = GetDeliveryByOrderRequest.newBuilder()
                .setOrderId(orderId)
                .build();
        return deliveryGrpc.getDeliveryByOrder(request);
    }

    public TrackResponse trackDelivery(String trackingNumber) {
        TrackRequest request = TrackRequest.newBuilder()
                .setTrackingNumber(trackingNumber)
                .build();
        return deliveryGrpc.trackDelivery(request);
    }
}
