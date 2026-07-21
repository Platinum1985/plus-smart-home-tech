package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;
import ru.yandex.practicum.state.OrderState;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService service;

    /**
     * Получить заказы пользователя.
     */
    @GetMapping
    public List<OrderDto> getClientOrders(@RequestParam String username) {
        log.info("Получение заказов пользователя: {}", username);
        return service.getClientOrders(username);
    }

    /**
     * Создать новый заказ в системе.
     */
    @PutMapping
    public OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request) {
        log.info("Создание нового заказа для корзины: {}", request.getShoppingCart().getShoppingCartId());
        return service.createNewOrder(request);
    }

    /**
     * Возврат заказа.
     */
    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody ProductReturnRequest request) {
        log.info("Возврат товаров для заказа: {}", request.getOrderId());
        return service.productReturn(request);
    }

    /**
     * Оплата заказа.
     */
    @PostMapping("/payment")
    public OrderDto payment(@RequestBody UUID orderId) {
        log.info("Обработка оплаты для заказа: {}", orderId);
        return service.payment(orderId);
    }

    /**
     * Оплата заказа произошла с ошибкой.
     */
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        log.info("Ошибка оплаты для заказа: {}", orderId);
        return service.paymentFailed(orderId);
    }

    /**
     * Доставка заказа.
     */
    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody UUID orderId) {
        log.info("Обработка доставки для заказа: {}", orderId);
        return service.delivery(orderId);
    }

    /**
     * Доставка заказа произошла с ошибкой.
     */
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        log.info("Ошибка доставки для заказа: {}", orderId);
        return service.deliveryFailed(orderId);
    }

    /**
     * Завершение заказа.
     */
    @PostMapping("/completed")
    public OrderDto complete(@RequestBody UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        return service.complete(orderId);
    }

    /**
     * Расчёт стоимости заказа.
     */
    @PostMapping("/calculate/total/{orderId}")
    public OrderDto calculateTotalCost(@PathVariable UUID orderId)  {
        log.info("Расчёт общей стоимости для заказа: {}", orderId);
        return service.calculateTotalCost(orderId);
    }

    /**
     * Расчёт стоимости доставки заказа.
     */
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderId);
        return service.calculateDeliveryCost(orderId);
    }

    /**
     * Сборка заказа.
     */
    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody UUID orderId) {
        log.info("Сборка заказа: {}", orderId);
        return service.assembly(orderId);
    }

    /**
     * Сборка заказа произошла с ошибкой.
     */
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        log.info("Ошибка сборки заказа: {}", orderId);
        return service.assemblyFailed(orderId);
    }

    @PatchMapping("/{orderId}/status")
    OrderDto updateOrderStatus(@PathVariable UUID orderId, @RequestParam OrderState status) {
        return service.updateOrderStatus(orderId, status);
    }

    /**
     * Поиск заказа по идентификатору.
     */
    @GetMapping("/{orderId}")
    public OrderDto findOrderById(@PathVariable UUID orderId) {
        log.info("Поиск заказа: {}", orderId);
        return service.findOrderById(orderId);
    }

    /**
     * Проверка существования заказа.
     */
    @GetMapping("/{orderId}/exists")
    public boolean isOrderExists(@PathVariable UUID orderId) {
        log.info("Проверка существования заказа: {}", orderId);
        return service.isOrderExists(orderId);
    }

}