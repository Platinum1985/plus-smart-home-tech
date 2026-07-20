package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.*;
import ru.yandex.practicum.dto.cart.entity.CartItem;
import ru.yandex.practicum.dto.cart.entity.ShoppingCart;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.order.entity.Order;
import ru.yandex.practicum.dto.order.entity.OrderProduct;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.state.DeliveryState;
import ru.yandex.practicum.state.OrderState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository rep;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;
    private final CartClient cartClient;

    /**
     * Получить заказы пользователя.
     */
    public List<OrderDto> getClientOrders(String username) {
        log.info("Получение заказов пользователя: {}", username);

        List<Order> orders = rep.findAllByUsername(username);

        return orders.stream().map(OrderMapper::toOrderDto).toList();
    }

    /**
     * Создать новый заказ в системе.
     */
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа для корзины: {}", request.getShoppingCart().getShoppingCartId());
        Order newOrder = newOrder(request);
        Order savedOrder = rep.save(newOrder);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Возврат заказа.
     */
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Возврат товаров для заказа: {}", request.getOrderId());
        Order order = rep.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Оформление возврата
        if(order.getOrderState().equals(OrderState.DONE) || order.getOrderState().equals(OrderState.DELIVERED)) {
            warehouseClient.acceptReturn(request.getProducts());
        }

        // Сохранение изменений
        order.setOrderState(OrderState.PRODUCT_RETURNED);
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Оплата заказа.
     */
    public OrderDto payment(UUID orderId) {
        log.info("Обработка оплаты для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Оплата
        try {
            PaymentDto paymentDto = paymentClient.payment(OrderMapper.toOrderDto(order));

            // Сохранение изменений
            order.setPaymentId(paymentDto.getPaymentId());
            order.setOrderState(OrderState.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            Order savedOrder = rep.save(order);

            return delivery(orderId);
        } catch (RuntimeException e) {
            return paymentFailed(orderId);
        }
    }

    /**
     * Оплата заказа произошла с ошибкой.
     */
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Ошибка оплаты для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Сохранение изменений
        order.setOrderState(OrderState.PAYMENT_FAILED);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Доставка заказа.
     */
    public OrderDto delivery(UUID orderId) {
        log.info("Обработка доставки для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        Order savedOrder;

        // Cоздание доставки
        try {
            DeliveryDto deliveryDto = DeliveryDto.builder()
                    .deliveryId(UUID.randomUUID())
                    .fromAddress(warehouseClient.getAddress())
                    .toAddress(new AddressDto(order.getDeliveryAddress()))
                    .orderId(orderId)
                    .deliveryState(DeliveryState.CREATED)
                    .build();
            deliveryClient.planDelivery(deliveryDto);

            // Сохранение изменений
            order.setDeliveryId(deliveryDto.getDeliveryId());
            order.setOrderState(OrderState.ON_DELIVERY);
            order.setUpdatedAt(LocalDateTime.now());
            savedOrder = rep.save(order);

            // Передача товаров курьеру
            ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                    .orderId(orderId)
                    .deliveryId(deliveryDto.getDeliveryId())
                    .build();
            warehouseClient.shippedToDelivery(request);

            return OrderMapper.toOrderDto(savedOrder);

        } catch (RuntimeException e) {
            return deliveryFailed(orderId);
        }

    }

    /**
     * Доставка заказа произошла с ошибкой.
     */
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Ошибка доставки для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Сохранение изменений
        order.setOrderState(OrderState.DELIVERY_FAILED);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Завершение заказа.
     */
    public OrderDto complete(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Сохранение изменений
        order.setOrderState(OrderState.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Расчёт стоимости заказа.
     */
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчёт общей стоимости для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Расчёт стоимости
        Double productPrice = paymentClient.productCost(OrderMapper.toOrderDto(order));
        Double deliveryPrice = order.getDeliveryPrice() == null
                ? calculateDeliveryCost(orderId).getDeliveryPrice()
                : order.getDeliveryPrice();
        Double totalPrice = productPrice + deliveryPrice;

        // Сохранение изменений
        order.setDeliveryPrice(deliveryPrice);
        order.setProductPrice(productPrice);
        order.setTotalPrice(totalPrice);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Расчёт стоимости доставки заказа.
     */
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        // Получение стоимости доставки
        Double deliveryPrice = deliveryClient.deliveryCost(OrderMapper.toOrderDto(order));

        // Сохранение изменений
        order.setDeliveryPrice(deliveryPrice);
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = rep.save(order);

        return OrderMapper.toOrderDto(updatedOrder);
    }

    /**
     * Сборка заказа.
     */
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        Order savedOrder;

        // Резервирование товара
        try {
            AssemblyProductsForOrderRequest assemblyRequest = OrderMapper.toAssemblyRequest(order);
            BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(assemblyRequest);

            // Наполнение заказа
            Order updatedOrder = updateAssemblyOrder(order, booked);
            savedOrder = rep.save(order);

        } catch (ProductInShoppingCartLowQuantityInWarehouse e) {
            // Обработка неудачного заказа
            return assemblyFailed(order.getId());
        }

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Сборка заказа произошла с ошибкой.
     */
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Ошибка сборки заказа: {}", orderId);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        order.setOrderState(OrderState.ASSEMBLY_FAILED);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderMapper.toOrderDto(rep.save(order));
    }

    /**
     * Поиск заказа по идентификатору.
     */
    public OrderDto findOrderById(UUID orderId) {
        log.info("Поиск заказа: {}", orderId);
        return OrderMapper.toOrderDto(
                rep.findById(orderId)
                        .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"))
        );
    }

    /**
     * Обновление статуса заказа.
     */
    public OrderDto updateOrderStatus(UUID orderId, OrderState state) {
        log.info("Обновление статуса заказа {} на {}", orderId, state);
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        order.setOrderState(state);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = rep.save(order);

        return OrderMapper.toOrderDto(savedOrder);
    }

    /**
     * Проверка статуса заказа.
     */
    public boolean isOrderExists(UUID orderId) {
        log.info("Проверка существования заказа: {}", orderId);
        return rep.existsById(orderId);
    }

    private Order newOrder(CreateNewOrderRequest request) {
        ShoppingCart cart = cartClient.getCartById(request.getShoppingCart().getShoppingCartId());
        String address = request.getDeliveryAddress().toString();
        UUID orderId = UUID.randomUUID();
        List<OrderProduct> products = cart.getItems().stream().
                map(item -> cartItemToOrderProduct(item, orderId))
                .toList();

        return Order.builder()
                .id(orderId)
                .username(cart.getUsername())
                .shoppingCartId(cart.getShoppingCartId())
                .paymentId(null)
                .deliveryId(null)
                .orderState(OrderState.NEW)
                .products(products)
                .deliveryWeight(null)
                .deliveryVolume(null)
                .fragile(null)
                .totalPrice(null)
                .productPrice(null)
                .deliveryPrice(null)
                .deliveryAddress(address)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Order updateAssemblyOrder(Order order, BookedProductsDto booked) {
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setFragile(booked.getFragile());
        order.setOrderState(OrderState.ASSEMBLED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    private OrderProduct cartItemToOrderProduct(CartItem cartItem, UUID orderId) {
        return OrderProduct.builder()
                .orderId(orderId)
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}