package org.acme.logistics.order.grpc;

import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.logistics.delivery.grpc.*;

import java.util.logging.Logger;

@ApplicationScoped
public class DeliveryGrpcClient {

    private static final Logger LOG = Logger.getLogger(DeliveryGrpcClient.class.getName());

    @GrpcClient("delivery")
    DeliveryService deliveryService;

    public DeliveryInfo createDelivery(Long orderId, String deliveryAddress) {
        LOG.info("gRPC: Створення доставки для замовлення #" + orderId);
        
        CreateDeliveryRequest request = CreateDeliveryRequest.newBuilder()
            .setOrderId(orderId)
            .setDeliveryAddress(deliveryAddress)
            .build();
        
        DeliveryInfo response = deliveryService.createDelivery(request).await().indefinitely();
        
        LOG.info("gRPC відповідь: доставка створена з ID=" + response.getId() + 
                ", tracking=" + response.getTrackingNumber());
        
        return response;
    }

    public DeliveryInfo getDeliveryByOrder(Long orderId) {
        LOG.info("gRPC: Отримання доставки для замовлення #" + orderId);
        
        OrderRequest request = OrderRequest.newBuilder()
            .setOrderId(orderId)
            .build();
        
        return deliveryService.getDeliveryByOrder(request).await().indefinitely();
    }

    public TrackResponse trackDelivery(String trackingNumber) {
        LOG.info("gRPC: Відстеження доставки: " + trackingNumber);
        
        TrackRequest request = TrackRequest.newBuilder()
            .setTrackingNumber(trackingNumber)
            .build();
        
        return deliveryService.trackDelivery(request).await().indefinitely();
    }
}
