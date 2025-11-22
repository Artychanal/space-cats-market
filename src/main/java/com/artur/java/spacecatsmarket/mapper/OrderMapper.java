package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Order;
import com.artur.java.spacecatsmarket.domain.OrderLine;
import com.artur.java.spacecatsmarket.dto.OrderLineResponseDto;
import com.artur.java.spacecatsmarket.dto.OrderRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderResponseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = CommonMappers.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lines", ignore = true)
    Order toEntity(OrderRequestDto dto);

    @Mapping(target = "lines", expression = "java(toLineResponses(order.getLines()))")
    OrderResponseDto toDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderLineResponseDto toLineDto(OrderLine line);

    List<OrderLineResponseDto> toLineResponses(List<OrderLine> lines);

    @AfterMapping
    default void linkLines(@MappingTarget Order order) {
        if (order.getLines() != null) {
            order.getLines().forEach(line -> line.setOrder(order));
        }
    }
}
