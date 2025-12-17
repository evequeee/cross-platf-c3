package org.acme.logistics.order.grpc;

import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.logistics.warehouse.grpc.*;

import java.util.logging.Logger;

@ApplicationScoped
public class WarehouseGrpcClient {

    private static final Logger LOG = Logger.getLogger(WarehouseGrpcClient.class.getName());

    @GrpcClient("warehouse")
    WarehouseService warehouseService;

    public boolean checkStockAvailability(Long productId, Integer quantity) {
        LOG.info("gRPC: Перевірка наявності товару #" + productId);
        
        StockRequest request = StockRequest.newBuilder()
            .setProductId(productId)
            .setQuantity(quantity)
            .build();
        
        StockResponse response = warehouseService.checkStock(request).await().indefinitely();
        
        LOG.info("gRPC відповідь: доступно=" + response.getAvailable() + 
                ", кількість=" + response.getAvailableQuantity());
        
        return response.getAvailable();
    }

    public boolean reserveStock(Long productId, Integer quantity) {
        LOG.info("gRPC: Резервування товару #" + productId);
        
        ReserveRequest request = ReserveRequest.newBuilder()
            .setProductId(productId)
            .setQuantity(quantity)
            .build();
        
        ReserveResponse response = warehouseService.reserveItems(request).await().indefinitely();
        
        LOG.info("gRPC відповідь: успішно=" + response.getSuccess() + 
                ", повідомлення=" + response.getMessage());
        
        return response.getSuccess();
    }

    public ItemInfo getItemInfo(Long productId) {
        LOG.info("gRPC: Отримання інформації про товар #" + productId);
        
        ItemRequest request = ItemRequest.newBuilder()
            .setProductId(productId)
            .build();
        
        return warehouseService.getItemInfo(request).await().indefinitely();
    }
}
