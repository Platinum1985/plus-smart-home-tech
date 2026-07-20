package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.warehouse.entity.WarehouseProduct;

import java.util.*;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {

    Optional<WarehouseProduct> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    @Modifying
    @Query("UPDATE WarehouseProduct w SET w.quantity = w.quantity + :quantity WHERE w.productId = :productId")
    void increaseQuantity(@Param("productId") UUID productId, @Param("quantity") Long quantity);

    @Modifying
    @Query("UPDATE WarehouseProduct w SET w.quantity = w.quantity - :quantity WHERE w.productId = :productId")
    void decreaseQuantity(@Param("productId") UUID productId, @Param("quantity") Long quantity);

    List<WarehouseProduct> findByProductIdIn(Set<UUID> productIds);
}