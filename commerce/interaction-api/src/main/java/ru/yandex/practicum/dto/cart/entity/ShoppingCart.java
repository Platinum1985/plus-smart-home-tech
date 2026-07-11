package ru.yandex.practicum.dto.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @Column(name = "shopping_cart_id", columnDefinition = "UUID")
    private UUID shoppingCartId;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "shoppingCart")
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
