package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.store.entity.Product;
import ru.yandex.practicum.state.ProductCategory;
import ru.yandex.practicum.state.ProductState;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategoryAndProductState(
            ProductCategory category,
            ProductState state,
            Pageable pageable
    );

    Page<Product> findByProductCategory(
            ProductCategory category,
            Pageable pageable
    );

    Optional<Product> findByProductIdAndProductState(UUID productId, ProductState state);

    boolean existsByProductId(UUID productId);

    void deleteByProductId(UUID productId);
}