package org.acme.logistics.warehouse.grpc;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import org.acme.logistics.warehouse.WarehouseItem;
import org.acme.logistics.warehouse.WarehouseRepository;

import java.util.Optional;

@GrpcService
public class WarehouseGrpcServiceImpl extends WarehouseGrpcServiceGrpc.WarehouseGrpcServiceImplBase {

    @Inject
    WarehouseRepository repository;

    @Override
    public void checkStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        Optional<WarehouseItem> item = repository.findByProductId(request.getProductId());
        
        StockResponse.Builder response = StockResponse.newBuilder();
        if (item.isPresent()) {
            WarehouseItem warehouseItem = item.get();
            boolean available = warehouseItem.hasEnoughStock(request.getQuantity());
            response.setAvailable(available)
                    .setAvailableQuantity(warehouseItem.getQuantityAvailable())
                    .setMessage(available ? "Товар в наявності" : "Недостатня кількість на складі");
        } else {
            response.setAvailable(false)
                    .setAvailableQuantity(0)
                    .setMessage("Товар не знайдено на складі");
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void reserveStock(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        ReserveResponse.Builder response = ReserveResponse.newBuilder();
        
        try {
            repository.reserveStock(request.getProductId(), request.getQuantity());
            response.setSuccess(true).setMessage("Товар зарезервовано успішно");
        } catch (Exception e) {
            response.setSuccess(false).setMessage("Помилка резервування: " + e.getMessage());
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void releaseStock(ReleaseRequest request, StreamObserver<ReleaseResponse> responseObserver) {
        ReleaseResponse.Builder response = ReleaseResponse.newBuilder();
        
        try {
            repository.releaseStock(request.getProductId(), request.getQuantity());
            response.setSuccess(true).setMessage("Резервування скасовано");
        } catch (Exception e) {
            response.setSuccess(false).setMessage("Помилка: " + e.getMessage());
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getItem(ItemRequest request, StreamObserver<ItemResponse> responseObserver) {
        Optional<WarehouseItem> item = repository.findByProductId(request.getProductId());
        
        ItemResponse.Builder response = ItemResponse.newBuilder();
        if (item.isPresent()) {
            WarehouseItem w = item.get();
            response.setFound(true)
                    .setId(w.id)
                    .setProductId(w.getProductId())
                    .setProductName(w.getProductName())
                    .setCategory(w.getCategory())
                    .setQuantityAvailable(w.getQuantityAvailable())
                    .setQuantityReserved(w.getQuantityReserved())
                    .setLocation(w.getLocation())
                    .setPricePerUnit(w.getPricePerUnit());
        } else {
            response.setFound(false);
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
