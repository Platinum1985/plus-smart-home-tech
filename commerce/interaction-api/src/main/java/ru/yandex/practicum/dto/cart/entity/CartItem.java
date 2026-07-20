package ru.yandex.practicum.dto.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_item_shopping_cart"))
    private ShoppingCart shoppingCart;

    @Column(name = "product_id", columnDefinition = "UUID", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
