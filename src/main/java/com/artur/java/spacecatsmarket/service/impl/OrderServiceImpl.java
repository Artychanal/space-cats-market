package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Order;
import com.artur.java.spacecatsmarket.domain.OrderLine;
import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.OrderRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderResponseDto;
import com.artur.java.spacecatsmarket.mapper.OrderMapper;
import com.artur.java.spacecatsmarket.repository.OrderRepository;
import com.artur.java.spacecatsmarket.repository.ProductRepository;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import com.artur.java.spacecatsmarket.service.OrderService;
import com.artur.java.spacecatsmarket.service.exception.OrderNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto create(OrderRequestDto request) {
        if (orderRepository.findByNumber(request.getNumber()).isPresent()) {
            throw new DataIntegrityViolationException("Order with number %s already exists".formatted(request.getNumber()));
        }
        Order order = orderMapper.toEntity(request);
        order.setCreatedAt(OffsetDateTime.now());
        order.setLines(buildLines(order, request));
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getByNumber(String number) {
        Order order = orderRepository.findByNumber(number)
                .orElseThrow(() -> new OrderNotFoundException("Order %s not found".formatted(number)));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSalesProjection> getTopSelling(Pageable pageable) {
        return productRepository.findTopSellingProducts(pageable);
    }

    private List<OrderLine> buildLines(Order order, OrderRequestDto request) {
        List<OrderLine> lines = new ArrayList<>();
        request.getLines().forEach(l -> {
            Product product = productRepository.findById(l.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product %s not found".formatted(l.getProductId())));
            lines.add(OrderLine.builder()
                    .order(order)
                    .product(product)
                    .qty(l.getQty())
                    .priceAtPurchase(l.getPriceAtPurchase())
                    .build());
        });
        return lines;
    }
}
