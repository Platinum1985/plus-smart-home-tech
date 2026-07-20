package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    private final DeliveryService service;

    /**
     * Создать новую доставку в БД.
     */
    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("Планирование доставки для заказа: {}", deliveryDto.getOrderId());
        return service.planDelivery(deliveryDto);
    }

    /**
     * Эмуляция успешной доставки товара.
     */
    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID orderId) {
        service.deliverySuccessful(orderId);
        log.info("Успешная доставка для заказа: {}", orderId);
    }

    /**
     * Эмуляция получения товара в доставку.
     */
    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID orderId) {
        service.deliveryPicked(orderId);
        log.info("Товар передан в доставку для заказа: {}", orderId);
    }

    /**
     * Эмуляция неудачного вручения товара.
     */
    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID orderId) {
        service.deliveryFailed(orderId);
        log.info("Неудачная доставка для заказа: {}", orderId);
    }

    /**
     * Расчёт полной стоимости доставки заказа.
     */
    @PostMapping("/cost")
    public Double deliveryCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderDto.getOrderId());
        return service.deliveryCost(orderDto);
    }



}