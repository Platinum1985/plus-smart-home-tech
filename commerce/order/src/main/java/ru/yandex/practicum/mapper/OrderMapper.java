package ru.yandex.practicum.mapper;


import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.entity.Order;
import ru.yandex.practicum.dto.order.entity.OrderProduct;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class OrderMapper {

    public OrderDto toOrderDto(Order order) {
        Map<UUID, Long> products = toProducts(order.getProducts());
        return OrderDto.builder()
                .orderId(order.getId())
                .shoppingCartId(order.getShoppingCartId())
                .products(products)
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getOrderState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .build();
    }

    public AssemblyProductsForOrderRequest toAssemblyRequest(Order order) {
        return AssemblyProductsForOrderRequest.builder()
                .orderId(order.getId())
                .products(toProducts(order.getProducts()))
                .build();
    }

    private Map<UUID, Long> toProducts(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .collect(Collectors.toMap(
                        OrderProduct::getProductId,  // ключ
                        OrderProduct::getQuantity    // значение
                ));
    }
}
