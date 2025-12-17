package org.acme.logistics.warehouse.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.logistics.warehouse.WarehouseRepository;
import org.acme.logistics.warehouse.WarehouseItem;

import java.util.logging.Logger;

@GrpcService
public class WarehouseGrpcService implements WarehouseService {

    private static final Logger LOG = Logger.getLogger(WarehouseGrpcService.class.getName());

    @Inject
    WarehouseRepository warehouseRepository;

    @Override
    public Uni<StockResponse> checkStock(StockRequest request) {
        LOG.info("gRPC: Перевірка наявності продукту #" + request.getProductId());
        
        boolean available = warehouseRepository.checkAvailability(
            request.getProductId(), 
            request.getQuantity()
        );
        
        return warehouseRepository.findByProductId(request.getProductId())
            .map(item -> StockResponse.newBuilder()
                .setAvailable(available)
                .setAvailableQuantity(item.getQuantityAvailable())
                .setReservedQuantity(item.getQuantityReserved())
                .setMessage(available ? "Товар доступний" : "Недостатня кількість")
                .build())
            .map(Uni::createFrom)
            .orElse(Uni.createFrom().item(
                StockResponse.newBuilder()
                    .setAvailable(false)
                    .setAvailableQuantity(0)
                    .setReservedQuantity(0)
                    .setMessage("Товар не знайдено")
                    .build()
            ));
    }

    @Override
    public Uni<ReserveResponse> reserveItems(ReserveRequest request) {
        LOG.info("gRPC: Резервування продукту #" + request.getProductId());
        
        try {
            warehouseRepository.reserveStock(request.getProductId(), request.getQuantity());
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Товар зарезервовано успішно")
                    .build()
            );
        } catch (Exception e) {
            LOG.warning("Помилка резервування: " + e.getMessage());
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Помилка резервування: " + e.getMessage())
                    .build()
            );
        }
    }

    @Override
    public Uni<ReserveResponse> releaseItems(ReserveRequest request) {
        LOG.info("gRPC: Зняття резервації продукту #" + request.getProductId());
        
        try {
            warehouseRepository.releaseStock(request.getProductId(), request.getQuantity());
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Резервацію знято успішно")
                    .build()
            );
        } catch (Exception e) {
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Помилка: " + e.getMessage())
                    .build()
            );
        }
    }

    @Override
    public Uni<ItemInfo> getItemInfo(ItemRequest request) {
        LOG.info("gRPC: Отримання інформації про продукт #" + request.getProductId());
        
        return warehouseRepository.findByProductId(request.getProductId())
            .map(item -> ItemInfo.newBuilder()
                .setId(item.getId())
                .setProductId(item.getProductId())
                .setProductName(item.getProductName())
                .setCategory(item.getCategory())
                .setQuantityAvailable(item.getQuantityAvailable())
                .setQuantityReserved(item.getQuantityReserved())
                .setLocation(item.getLocation())
                .setWarehouseZone(item.getWarehouseZone())
                .setPricePerUnit(item.getPricePerUnit())
                .build())
            .map(Uni::createFrom)
            .orElse(Uni.createFrom().item(ItemInfo.newBuilder().build()));
    }
}
