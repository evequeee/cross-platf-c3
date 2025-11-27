package org.acme.grpc.warehouse;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.warehouse.WarehouseRepository;
import org.acme.warehouse.WarehouseItem;

@GrpcService
public class WarehouseGrpcService implements WarehouseService {

    @Inject
    WarehouseRepository warehouseRepository;

    @Override
    public Uni<StockResponse> checkStock(StockRequest request) {
        boolean available = warehouseRepository.checkAvailability(
            request.getProductId(), 
            request.getQuantity()
        );
        
        int availableQty = warehouseRepository.findByProductId(request.getProductId())
            .map(WarehouseItem::getQuantityAvailable)
            .orElse(0);

        return Uni.createFrom().item(
            StockResponse.newBuilder()
                .setAvailable(available)
                .setAvailableQuantity(availableQty)
                .setMessage(available ? "Товар доступний" : "Недостатня кількість")
                .build()
        );
    }

    @Override
    public Uni<ReserveResponse> reserveItems(ReserveRequest request) {
        try {
            warehouseRepository.reserveStock(request.getProductId(), request.getQuantity());
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Товар зарезервовано успішно")
                    .build()
            );
        } catch (Exception e) {
            return Uni.createFrom().item(
                ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Помилка резервування: " + e.getMessage())
                    .build()
            );
        }
    }

    @Override
    public Uni<ItemInfo> getItemInfo(ItemRequest request) {
        return Uni.createFrom().item(() -> 
            warehouseRepository.findByProductId(request.getProductId())
                .map(item -> ItemInfo.newBuilder()
                    .setId(item.getId())
                    .setProductId(item.getProductId())
                    .setProductName(item.getProductName())
                    .setQuantityAvailable(item.getQuantityAvailable())
                    .setLocation(item.getLocation())
                    .setPricePerUnit(item.getPricePerUnit())
                    .build()
                )
                .orElse(ItemInfo.newBuilder().build())
        );
    }
}
