package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.entity.Payment;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.state.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository rep;

    /**
     * Формирование оплаты для заказа (переход в платежный шлюз).
     */
    public PaymentDto payment(OrderDto orderDto) {
        log.info("Формирование оплаты для заказа: {}", orderDto.getOrderId());

        Double totalPrice, deliveryTotal, productTotal;
        totalPrice = orderDto.getTotalPrice();
        deliveryTotal = orderDto.getDeliveryPrice();
        productTotal = orderDto.getProductPrice();

        if(totalPrice == null ||
                deliveryTotal == null ||
                productTotal == null)
            throw new NotEnoughInfoInOrderToCalculateException("Не хватает данных платежа");

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderDto.getOrderId())
                .paymentState(PaymentStatus.PENDING)
                .totalPayment(getTotalCost(orderDto))
                .deliveryTotal(deliveryTotal)
                .feeTotal(totalPrice * 0.15)
                .productTotal(productTotal)
                .paymentDescription("text")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment saverPayment = rep.save(payment);

        return PaymentMapper.toDto(saverPayment);
    }

    /**
     * Расчёт полной стоимости заказа.
     */
    public Double getTotalCost(OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа: {}", orderDto.getOrderId());
        return orderDto.getTotalPrice() * 1.15;
    }

    /**
     * Метод для эмуляции успешной оплаты в платежного шлюза.
     */
    public void paymentSuccess(UUID paymentId) {
        log.info("Успешная оплата для платежа: {}", paymentId);
        Payment payment = rep.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Платёж не найден")
        );
        payment.setPaymentState(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = rep.save(payment);

        log.info("Успешная оплата: {}", savedPayment.toString());
    }

    /**
     * Расчёт стоимости товаров в заказе.
     */
    public Double productCost(OrderDto orderDto) {
        log.info("Расчёт стоимости товаров для заказа: {}", orderDto.getOrderId());
        return orderDto.getProductPrice();
    }

    /**
     * Метод для эмуляции отказа в оплате платежного шлюза.
     */
    public void paymentFailed(UUID paymentId) {
        log.info("Ошибка оплаты для платежа: {}", paymentId);
        Payment payment = rep.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Платёж не найден")
        );

        payment.setPaymentState(PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = rep.save(payment);

    }

    /**
     * Поиск платежа по идентификатору.
     */
    public PaymentDto findPaymentById(UUID paymentId) {
        log.info("Поиск платежа: {}", paymentId);
        Payment payment = rep.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Платёж не найден")
        );
        return PaymentMapper.toDto(payment);
    }

    /**
     * Обновление статуса платежа.
     */
    public PaymentDto updatePaymentStatus(UUID paymentId, PaymentStatus status) {
        log.info("Обновление статуса платежа {} на {}", paymentId, status);
        Payment payment = rep.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("Платёж не найден")
        );
        payment.setPaymentState(status);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = rep.save(payment);

        return PaymentMapper.toDto(savedPayment);
    }
}