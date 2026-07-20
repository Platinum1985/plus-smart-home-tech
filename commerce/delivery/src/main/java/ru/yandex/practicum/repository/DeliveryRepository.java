package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.entity.Delivery;
import ru.yandex.practicum.state.DeliveryState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByOrderId(UUID orderId);

    List<Delivery> findAllByDeliveryState(DeliveryState state);

    boolean existsByOrderId(UUID orderId);

    void deleteByOrderId(UUID orderId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Delivery d
            SET d.deliveryState = :state
            WHERE d.orderId = :orderId
            """)
    void updateDeliveryState(
            @Param("orderId") UUID orderId,
            @Param("state") DeliveryState state
    );
}