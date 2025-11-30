package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Order;
import com.artur.java.spacecatsmarket.domain.OrderLine;
import com.artur.java.spacecatsmarket.persistence.entity.OrderEntity;
import com.artur.java.spacecatsmarket.persistence.entity.OrderLineEntity;
import com.artur.java.spacecatsmarket.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

    default OrderEntity toEntity(Order domain, List<ProductEntity> products) {
        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .number(domain.getNumber())
                .customerEmail(domain.getCustomerEmail())
                .createdAt(domain.getCreatedAt())
                .lines(new ArrayList<>())
                .build();

        if (domain.getLines() != null) {
            for (OrderLine line : domain.getLines()) {
                ProductEntity product = products.stream()
                        .filter(p -> p.getId().equals(line.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Product %s not found for order".formatted(line.getProductId())));
                OrderLineEntity lineEntity = OrderLineEntity.builder()
                        .id(line.getId())
                        .order(entity)
                        .product(product)
                        .qty(line.getQty())
                        .priceAtPurchase(line.getPriceAtPurchase())
                        .build();
                entity.getLines().add(lineEntity);
            }
        }
        return entity;
    }

    default Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        List<OrderLine> lines = new ArrayList<>();
        if (entity.getLines() != null) {
            for (OrderLineEntity l : entity.getLines()) {
                lines.add(OrderLine.builder()
                        .id(l.getId())
                        .productId(l.getProduct().getId())
                        .qty(l.getQty())
                        .priceAtPurchase(l.getPriceAtPurchase())
                        .build());
            }
        }
        return Order.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .customerEmail(entity.getCustomerEmail())
                .createdAt(entity.getCreatedAt())
                .lines(lines)
                .build();
    }
}
