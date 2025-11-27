package org.acme.grpc.delivery;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.delivery.Delivery;
import org.acme.delivery.DeliveryRepository;
import org.acme.delivery.DeliveryStatus;

import java.time.LocalDateTime;

@GrpcService
public class DeliveryGrpcService implements DeliveryService {

    @Inject
    DeliveryRepository deliveryRepository;

    @Override
    public Uni<DeliveryInfo> createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = new Delivery(
            null,
            request.getOrderId(),
            "Водій (gRPC)",
            "+380509999999",
            request.getPickupAddress(),
            request.getDeliveryAddress(),
            LocalDateTime.now().plusHours(3)
        );

        Delivery saved = deliveryRepository.save(delivery);
        
        return Uni.createFrom().item(toDeliveryInfo(saved));
    }

    @Override
    public Uni<DeliveryInfo> updateDeliveryStatus(UpdateStatusRequest request) {
        DeliveryStatus status = DeliveryStatus.valueOf(request.getStatus());
        Delivery updated = deliveryRepository.updateStatus(request.getDeliveryId(), status);
        
        if (updated != null) {
            return Uni.createFrom().item(toDeliveryInfo(updated));
        }
        
        return Uni.createFrom().item(DeliveryInfo.newBuilder().build());
    }

    @Override
    public Uni<DeliveryInfo> getDeliveryByOrder(OrderRequest request) {
        return Uni.createFrom().item(() ->
            deliveryRepository.findByOrderId(request.getOrderId())
                .map(this::toDeliveryInfo)
                .orElse(DeliveryInfo.newBuilder().build())
        );
    }

    @Override
    public Uni<TrackResponse> trackDelivery(TrackRequest request) {
        return Uni.createFrom().item(() ->
            deliveryRepository.findById(request.getDeliveryId())
                .map(delivery -> TrackResponse.newBuilder()
                    .setDeliveryId(delivery.getId())
                    .setCurrentStatus(delivery.getStatus().name())
                    .setCurrentLocation("В дорозі")
                    .setEstimatedArrival(delivery.getEstimatedDeliveryTime().toString())
                    .build()
                )
                .orElse(TrackResponse.newBuilder().build())
        );
    }

    private DeliveryInfo toDeliveryInfo(Delivery delivery) {
        return DeliveryInfo.newBuilder()
            .setId(delivery.getId())
            .setOrderId(delivery.getOrderId())
            .setDriverName(delivery.getDriverName())
            .setDriverPhone(delivery.getDriverPhone())
            .setPickupAddress(delivery.getPickupAddress())
            .setDeliveryAddress(delivery.getDeliveryAddress())
            .setStatus(delivery.getStatus().name())
            .setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime().toString())
            .build();
    }
}
