package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.entity.Order;
import ru.yandex.practicum.state.OrderState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByUsernameOrderByCreatedAtDesc(String username);

    Optional<Order> findByShoppingCartId(UUID shoppingCartId);

    Optional<Order> findByPaymentId(UUID paymentId);

    Optional<Order> findByDeliveryId(UUID deliveryId);

    List<Order> findAllByOrderState(OrderState state);

    boolean existsByShoppingCartId(UUID shoppingCartId);

    void deleteByShoppingCartId(UUID shoppingCartId);

    List<Order> findAllByUsername(String username);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Order o
            SET o.orderState = :state
            WHERE o.id = :orderId
            """)
    void updateOrderState(
            @Param("orderId") UUID orderId,
            @Param("state") OrderState state
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE Order o
            SET o.paymentId = :paymentId, o.orderState = :state
            WHERE o.id = :orderId
            """)
    void updatePaymentIdAndState(
            @Param("orderId") UUID orderId,
            @Param("paymentId") UUID paymentId,
            @Param("state") OrderState state
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE Order o
            SET o.deliveryId = :deliveryId, o.orderState = :state
            WHERE o.id = :orderId
            """)
    void updateDeliveryIdAndState(
            @Param("orderId") UUID orderId,
            @Param("deliveryId") UUID deliveryId,
            @Param("state") OrderState state
    );

}