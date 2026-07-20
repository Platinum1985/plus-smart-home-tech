package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.entity.Delivery;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.entity.Order;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.state.DeliveryState;
import ru.yandex.practicum.state.OrderState;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository rep;

    private final OrderClient orderClient;

    /**
     * Создать новую доставку в БД.
     */
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Планирование доставки для заказа: {}", deliveryDto.getOrderId());
        Delivery delivery = Delivery.builder()
                .id(deliveryDto.getDeliveryId())
                .orderId(deliveryDto.getOrderId())
                .fromAddress(deliveryDto.getFromAddress().toString())
                .toAddress(deliveryDto.getToAddress().toString())
                .deliveryState(deliveryDto.getDeliveryState())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Delivery savedDelivery = rep.save(delivery);
        return DeliveryMapper.toDto(savedDelivery);
    }

    /**
     * Эмуляция успешной доставки товара.
     */
    public void deliverySuccessful(UUID orderId) {
        log.info("Успешная доставка для заказа: {}", orderId);
        Delivery delivery = rep.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));
        // Обновление статуса в заказе
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        delivery.setUpdatedAt(LocalDateTime.now());
        rep.save(delivery);

        // Обновление статуса в заказе
        orderClient.updateOrderStatus(orderId, OrderState.DELIVERED);
    }

    /**
     * Эмуляция получения товара в доставку.
     */
    public void deliveryPicked(UUID orderId) {
        log.info("Товар передан в доставку для заказа: {}", orderId);
        // TODO: Implement delivery picked logic
        Delivery delivery = rep.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));
        // Обновление статуса в заказе
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        delivery.setUpdatedAt(LocalDateTime.now());
        rep.save(delivery);
    }

    /**
     * Эмуляция не удачной доставки товара.
     */
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = rep.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));
        // Обновление статуса в заказе
        delivery.setDeliveryState(DeliveryState.FAILED);
        delivery.setUpdatedAt(LocalDateTime.now());
        rep.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    /**
     * Расчёт полной стоимости доставки заказа.
     */
    public Double deliveryCost(OrderDto orderDto) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderDto.getOrderId());
        Double deliveryCost =
                orderDto.getDeliveryVolume() * 0.8 +
                        orderDto.getDeliveryWeight() * 0.5 +
                        (orderDto.getFragile() ? 1 : 0) * 30;

        return deliveryCost;
    }

    /**
     * Поиск доставки по идентификатору заказа.
     */
    public DeliveryDto findDeliveryByOrderId(UUID orderId) {
        log.info("Поиск доставки для заказа: {}", orderId);
        Delivery delivery = rep.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена для заказа"));
        return DeliveryMapper.toDto(delivery);
    }

    /**
     * Обновление статуса доставки.
     */
    public DeliveryDto updateDeliveryStatus(UUID deliveryId, DeliveryState state) {
        log.info("Обновление статуса доставки {} на {}", deliveryId, state);
        Delivery delivery = rep.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));

        delivery.setDeliveryState(state);
        delivery.setUpdatedAt(LocalDateTime.now());
        Delivery savedDelivery = rep.save(delivery);

        return DeliveryMapper.toDto(savedDelivery);
    }

}