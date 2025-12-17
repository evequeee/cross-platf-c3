package org.acme.logistics.delivery.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.logistics.delivery.Delivery;
import org.acme.logistics.delivery.DeliveryRepository;
import org.acme.logistics.delivery.DeliveryService;
import org.acme.logistics.delivery.DeliveryStatus;

import java.util.logging.Logger;

@GrpcService
public class DeliveryGrpcService implements DeliveryService {

    private static final Logger LOG = Logger.getLogger(DeliveryGrpcService.class.getName());

    @Inject
    DeliveryRepository deliveryRepository;

    @Inject
    org.acme.logistics.delivery.DeliveryService deliveryService;

    @Override
    public Uni<DeliveryInfo> createDelivery(CreateDeliveryRequest request) {
        LOG.info("gRPC: Створення доставки для замовлення #" + request.getOrderId());
        
        try {
            Delivery delivery = deliveryService.createDelivery(
                request.getOrderId(),
                request.getDeliveryAddress()
            );
            return Uni.createFrom().item(toDeliveryInfo(delivery));
        } catch (Exception e) {
            LOG.severe("Помилка створення доставки: " + e.getMessage());
            return Uni.createFrom().item(DeliveryInfo.newBuilder().build());
        }
    }

    @Override
    public Uni<DeliveryInfo> updateDeliveryStatus(UpdateStatusRequest request) {
        LOG.info("gRPC: Оновлення статусу доставки #" + request.getDeliveryId());
        
        try {
            DeliveryStatus status = DeliveryStatus.valueOf(request.getStatus());
            deliveryService.updateDeliveryStatus(request.getDeliveryId(), status);
            
            return deliveryRepository.findById(request.getDeliveryId())
                .map(this::toDeliveryInfo)
                .map(Uni::createFrom)
                .orElse(Uni.createFrom().item(DeliveryInfo.newBuilder().build()));
        } catch (Exception e) {
            LOG.severe("Помилка оновлення статусу: " + e.getMessage());
            return Uni.createFrom().item(DeliveryInfo.newBuilder().build());
        }
    }

    @Override
    public Uni<DeliveryInfo> getDeliveryByOrder(OrderRequest request) {
        LOG.info("gRPC: Отримання доставки для замовлення #" + request.getOrderId());
        
        return deliveryRepository.findByOrderId(request.getOrderId())
            .map(this::toDeliveryInfo)
            .map(Uni::createFrom)
            .orElse(Uni.createFrom().item(DeliveryInfo.newBuilder().build()));
    }

    @Override
    public Uni<TrackResponse> trackDelivery(TrackRequest request) {
        LOG.info("gRPC: Відстеження доставки: " + request.getTrackingNumber());
        
        return deliveryRepository.findByTrackingNumber(request.getTrackingNumber())
            .map(delivery -> TrackResponse.newBuilder()
                .setTrackingNumber(delivery.getTrackingNumber())
                .setCurrentStatus(delivery.getStatus().toString())
                .setCurrentLocation(delivery.getCurrentLocation() != null ? delivery.getCurrentLocation() : "")
                .setEstimatedArrival(delivery.getEstimatedDeliveryTime() != null ? 
                    delivery.getEstimatedDeliveryTime().toString() : "")
                .setDriverName(delivery.getDriverName())
                .setDriverPhone(delivery.getDriverPhone())
                .build())
            .map(Uni::createFrom)
            .orElse(Uni.createFrom().item(TrackResponse.newBuilder().build()));
    }

    private DeliveryInfo toDeliveryInfo(Delivery delivery) {
        return DeliveryInfo.newBuilder()
            .setId(delivery.getId())
            .setOrderId(delivery.getOrderId())
            .setDriverName(delivery.getDriverName())
            .setDriverPhone(delivery.getDriverPhone())
            .setVehicleNumber(delivery.getVehicleNumber() != null ? delivery.getVehicleNumber() : "")
            .setDeliveryAddress(delivery.getDeliveryAddress())
            .setStatus(delivery.getStatus().toString())
            .setTrackingNumber(delivery.getTrackingNumber())
            .setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime() != null ? 
                delivery.getEstimatedDeliveryTime().toString() : "")
            .setCurrentLocation(delivery.getCurrentLocation() != null ? delivery.getCurrentLocation() : "")
            .build();
    }
}
