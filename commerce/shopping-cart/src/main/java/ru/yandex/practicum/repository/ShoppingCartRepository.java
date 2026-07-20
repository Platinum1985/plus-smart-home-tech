package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.cart.entity.ShoppingCart;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByUsernameAndActiveTrue(String username);

    Optional<ShoppingCart> findByShoppingCartIdAndActiveTrue(UUID shoppingCartId);

    @Modifying
    @Query("UPDATE ShoppingCart c SET c.active = false WHERE c.username = :username AND c.active = true")
    void deactivateActiveCartByUsername(@Param("username") String username);

    boolean existsByUsernameAndActiveTrue(String username);
}