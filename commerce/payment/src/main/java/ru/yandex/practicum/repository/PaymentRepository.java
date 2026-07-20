package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.payment.entity.Payment;
import ru.yandex.practicum.state.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findAllByPaymentState(PaymentStatus state);

    boolean existsByOrderId(UUID orderId);

    void deleteByOrderId(UUID orderId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Payment p
            SET p.paymentState = :state
            WHERE p.orderId = :orderId
            """)
    void updatePaymentState(
            @Param("orderId") UUID orderId,
            @Param("state") PaymentStatus state
    );

}