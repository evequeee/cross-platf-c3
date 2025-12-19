package org.acme.logistics.order.client;

import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.logistics.warehouse.grpc.*;

@ApplicationScoped
public class WarehouseGrpcClient {

    @GrpcClient("warehouse")
    WarehouseGrpcServiceGrpc.WarehouseGrpcServiceBlockingStub warehouseGrpc;

    public boolean checkStock(Long productId, Integer quantity) {
        StockRequest request = StockRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();
        StockResponse response = warehouseGrpc.checkStock(request);
        return response.getAvailable();
    }

    public boolean reserveStock(Long productId, Integer quantity) {
        ReserveRequest request = ReserveRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();
        ReserveResponse response = warehouseGrpc.reserveStock(request);
        return response.getSuccess();
    }

    public boolean releaseStock(Long productId, Integer quantity) {
        ReleaseRequest request = ReleaseRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();
        ReleaseResponse response = warehouseGrpc.releaseStock(request);
        return response.getSuccess();
    }

    public ItemResponse getItem(Long productId) {
        ItemRequest request = ItemRequest.newBuilder()
                .setProductId(productId)
                .build();
        return warehouseGrpc.getItem(request);
    }
}
