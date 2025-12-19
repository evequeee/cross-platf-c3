package org.acme.logistics.delivery.grpc;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import org.acme.logistics.delivery.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class DeliveryGrpcServiceImpl extends DeliveryGrpcServiceGrpc.DeliveryGrpcServiceImplBase {

    @Inject
    DeliveryRepository repository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void createDelivery(CreateDeliveryRequest request, StreamObserver<DeliveryResponse> responseObserver) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setPickupAddress(request.getPickupAddress());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusDays(3));
        
        Delivery saved = repository.save(delivery);
        
        DeliveryResponse response = buildDeliveryResponse(saved, true, "Доставку створено");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDelivery(GetDeliveryRequest request, StreamObserver<DeliveryResponse> responseObserver) {
        Optional<Delivery> delivery = repository.findById(request.getId());
        
        DeliveryResponse response;
        if (delivery.isPresent()) {
            response = buildDeliveryResponse(delivery.get(), true, "OK");
        } else {
            response = DeliveryResponse.newBuilder().setFound(false).setMessage("Доставку не знайдено").build();
        }
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDeliveryByOrder(GetDeliveryByOrderRequest request, StreamObserver<DeliveryResponse> responseObserver) {
        Optional<Delivery> delivery = repository.findByOrderId(request.getOrderId());
        
        DeliveryResponse response;
        if (delivery.isPresent()) {
            response = buildDeliveryResponse(delivery.get(), true, "OK");
        } else {
            response = DeliveryResponse.newBuilder().setFound(false).setMessage("Доставку для замовлення не знайдено").build();
        }
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateStatus(UpdateStatusRequest request, StreamObserver<DeliveryResponse> responseObserver) {
        Optional<Delivery> optDelivery = repository.findById(request.getId());
        
        DeliveryResponse response;
        if (optDelivery.isPresent()) {
            Delivery delivery = optDelivery.get();
            delivery.setStatus(DeliveryStatus.valueOf(request.getStatus()));
            if (!request.getCurrentLocation().isEmpty()) {
                delivery.setCurrentLocation(request.getCurrentLocation());
            }
            if (request.getStatus().equals("DELIVERED")) {
                delivery.setActualDeliveryTime(LocalDateTime.now());
            }
            Delivery updated = repository.save(delivery);
            response = buildDeliveryResponse(updated, true, "Статус оновлено");
        } else {
            response = DeliveryResponse.newBuilder().setFound(false).setMessage("Доставку не знайдено").build();
        }
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void trackDelivery(TrackRequest request, StreamObserver<TrackResponse> responseObserver) {
        Optional<Delivery> delivery = repository.findByTrackingNumber(request.getTrackingNumber());
        
        TrackResponse.Builder response = TrackResponse.newBuilder();
        if (delivery.isPresent()) {
            Delivery d = delivery.get();
            response.setFound(true)
                    .setTrackingNumber(d.getTrackingNumber())
                    .setStatus(d.getStatus().name())
                    .setCurrentLocation(d.getCurrentLocation() != null ? d.getCurrentLocation() : "Невідомо");
            if (d.getEstimatedDeliveryTime() != null) {
                response.setEstimatedDeliveryTime(d.getEstimatedDeliveryTime().format(formatter));
            }
        } else {
            response.setFound(false);
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private DeliveryResponse buildDeliveryResponse(Delivery d, boolean found, String message) {
        DeliveryResponse.Builder builder = DeliveryResponse.newBuilder()
                .setFound(found)
                .setMessage(message)
                .setId(d.getId())
                .setOrderId(d.getOrderId())
                .setTrackingNumber(d.getTrackingNumber() != null ? d.getTrackingNumber() : "")
                .setStatus(d.getStatus().name())
                .setPickupAddress(d.getPickupAddress() != null ? d.getPickupAddress() : "")
                .setDeliveryAddress(d.getDeliveryAddress() != null ? d.getDeliveryAddress() : "");
        
        if (d.getDriverName() != null) builder.setDriverName(d.getDriverName());
        if (d.getDriverPhone() != null) builder.setDriverPhone(d.getDriverPhone());
        if (d.getCurrentLocation() != null) builder.setCurrentLocation(d.getCurrentLocation());
        if (d.getEstimatedDeliveryTime() != null) {
            builder.setEstimatedDeliveryTime(d.getEstimatedDeliveryTime().format(formatter));
        }
        
        return builder.build();
    }
}
