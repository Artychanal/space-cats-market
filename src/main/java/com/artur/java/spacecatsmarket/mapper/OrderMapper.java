package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Order;
import com.artur.java.spacecatsmarket.domain.OrderLine;
import com.artur.java.spacecatsmarket.dto.OrderLineRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderLineResponseDto;
import com.artur.java.spacecatsmarket.dto.OrderRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = CommonMappers.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toDomain(OrderRequestDto dto);

    @Mapping(target = "id", ignore = true)
    OrderLine toLine(OrderLineRequestDto dto);

    @Mapping(target = "lines", expression = "java(toLineResponses(order.getLines()))")
    OrderResponseDto toDto(Order order);

    OrderLineResponseDto toLineDto(OrderLine line);

    List<OrderLineResponseDto> toLineResponses(List<OrderLine> lines);
}
