package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.order.entity.OrderProduct;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {

    List<OrderProduct> findAllByOrderId(UUID orderId);

    void deleteAllByOrderId(UUID orderId);
}