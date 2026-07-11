package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.PageableObject;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SortObject;
import ru.yandex.practicum.dto.store.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }

    public static PageProductDto toPageDto(Page<Product> page) {
        if (page == null) {
            return null;
        }

        // Конвертируем список продуктов в ProductDto
        List<ProductDto> content = page.getContent().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());

        // Конвертируем Pageable в PageableObject
        PageableObject pageableObject = toPageableObject(page.getPageable());

        // Конвертируем Sort в List<SortObject>
        List<SortObject> sortObjects = toSortObjects(page.getSort());

        return PageProductDto.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .size(page.getSize())
                .content(content)
                .number(page.getNumber())
                .sort(sortObjects)
                .numberOfElements(page.getNumberOfElements())
                .pageable(pageableObject)
                .empty(page.isEmpty())
                .build();
    }

    private PageableObject toPageableObject(Pageable pageable) {
        if (pageable == null) {
            return null;
        }

        return PageableObject.builder()
                .offset(pageable.getOffset())
                .sort(toSortObjects(pageable.getSort()))
                .unpaged(pageable.isUnpaged())
                .paged(pageable.isPaged())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    private List<SortObject> toSortObjects(Sort sort) {
        if (sort == null || sort.isEmpty()) {
            return List.of();
        }

        return sort.stream()
                .map(order -> SortObject.builder()
                        .direction(order.getDirection().name())
                        .property(order.getProperty())
                        .ascending(order.isAscending())
                        .ignoreCase(order.isIgnoreCase())
                        .nullHandling(order.getNullHandling().name())
                        .build())
                .collect(Collectors.toList());
    }
}
