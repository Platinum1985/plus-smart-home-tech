package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;
import ru.yandex.practicum.state.PaymentStatus;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService service;

    /**
     * Формирование оплаты для заказа (переход в платежный шлюз).
     */
    @PostMapping
    public PaymentDto payment(@RequestBody OrderDto orderDto) {
        log.info("Формирование оплаты для заказа: {}", orderDto.getOrderId());
        return service.payment(orderDto);
    }

    /**
     * Расчёт полной стоимости заказа.
     */
    @PostMapping("/totalCost")
    public Double getTotalCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа: {}", orderDto.getOrderId());
        return service.getTotalCost(orderDto);
    }

    /**
     * Метод для эмуляции успешной оплаты в платежного шлюза.
     */
    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody UUID paymentId) {
        service.paymentSuccess(paymentId);
        log.info("Успешная оплата для платежа: {}", paymentId);
    }

    /**
     * Расчёт стоимости товаров в заказе.
     */
    @PostMapping("/productCost")
    public Double productCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт стоимости товаров для заказа: {}", orderDto.getOrderId());
        return service.productCost(orderDto);
    }

    /**
     * Метод для эмуляции отказа в оплате платежного шлюза.
     */
    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        service.paymentFailed(paymentId);
        log.info("Ошибка оплаты для платежа: {}", paymentId);
    }

    /**
     * Поиск платежа по идентификатору.
     */
    @GetMapping("/{paymentId}")
    public PaymentDto findPaymentById(@PathVariable UUID paymentId) {
        log.info("Поиск платежа: {}", paymentId);
        return service.findPaymentById(paymentId);
    }

    /**
     * Обновление статуса платежа.
     */
    @PatchMapping("/{paymentId}/status")
    public PaymentDto updatePaymentStatus(
            @PathVariable UUID paymentId,
            @RequestParam PaymentStatus status) {
        log.info("Обновление статуса платежа {} на {}", paymentId, status);
        return service.updatePaymentStatus(paymentId, status);
    }

}