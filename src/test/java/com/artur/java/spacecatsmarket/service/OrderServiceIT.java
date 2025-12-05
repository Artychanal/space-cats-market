package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("no-auth")
class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("Should create order and fetch it by natural id")
    void createAndFetchOrder() {
        Products products = seedCatalog();
        OrderResponseDto order = orderService.create(OrderRequestDto.builder()
                .number("ORD-001")
                .customerEmail("pilot@cosmo.cats")
                .lines(List.of(
                        OrderLineRequestDto.builder()
                                .productId(products.a().getId())
                                .qty(2)
                                .priceAtPurchase(BigDecimal.valueOf(12.5))
                                .build()))
                .build());

        assertThat(order.getId()).isNotNull();
        OrderResponseDto fromDb = orderService.getByNumber("ORD-001");
        assertThat(fromDb.getCustomerEmail()).isEqualTo("pilot@cosmo.cats");
        assertThat(fromDb.getLines()).hasSize(1);
        assertThat(fromDb.getLines().getFirst().getProductId()).isEqualTo(products.a().getId());
    }

    @Test
    @DisplayName("Should aggregate product sales with projection")
    void getTopSellingProducts() {
        Products products = seedCatalog();
        orderService.create(OrderRequestDto.builder()
                .number("ORD-100")
                .customerEmail("first@cosmo.cats")
                .lines(List.of(
                        OrderLineRequestDto.builder()
                                .productId(products.a().getId())
                                .qty(3)
                                .priceAtPurchase(BigDecimal.valueOf(12.5))
                                .build(),
                        OrderLineRequestDto.builder()
                                .productId(products.b().getId())
                                .qty(1)
                                .priceAtPurchase(BigDecimal.valueOf(30))
                                .build()))
                .build());
        orderService.create(OrderRequestDto.builder()
                .number("ORD-101")
                .customerEmail("second@cosmo.cats")
                .lines(List.of(
                        OrderLineRequestDto.builder()
                                .productId(products.b().getId())
                                .qty(5)
                                .priceAtPurchase(BigDecimal.valueOf(30))
                                .build()))
                .build());

        Page<ProductSalesProjection> topSelling = orderService.getTopSelling(PageRequest.of(0, 5));

        assertThat(topSelling.getContent()).hasSize(2);
        assertThat(topSelling.getContent().getFirst().productId()).isEqualTo(products.b().getId());
        assertThat(topSelling.getContent().getFirst().totalQuantity()).isEqualTo(6L);
    }

    @Test
    @DisplayName("Should reject duplicated order numbers")
    void rejectDuplicateOrders() {
        Products products = seedCatalog();
        orderService.create(OrderRequestDto.builder()
                .number("ORD-777")
                .customerEmail("lucky@cosmo.cats")
                .lines(List.of(
                        OrderLineRequestDto.builder()
                                .productId(products.a().getId())
                                .qty(1)
                                .priceAtPurchase(BigDecimal.valueOf(12.5))
                                .build()))
                .build());

        assertThatThrownBy(() -> orderService.create(OrderRequestDto.builder()
                .number("ORD-777")
                .customerEmail("another@cosmo.cats")
                .lines(List.of(
                        OrderLineRequestDto.builder()
                                .productId(products.a().getId())
                                .qty(1)
                                .priceAtPurchase(BigDecimal.valueOf(12.5))
                                .build()))
                .build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private record Products(ProductResponseDto a, ProductResponseDto b) {}

    private Products seedCatalog() {
        categoryService.createCategory(CategoryRequestDto.builder().code("FOOD").title("Food").build());
        categoryService.createCategory(CategoryRequestDto.builder().code("GEAR").title("Gear").build());
        ProductResponseDto productA = productService.createProduct(ProductRequestDto.builder()
                .name("Astro Tuna")
                .description("Premium tuna for space cats")
                .price(BigDecimal.valueOf(12.5))
                .currency("USD")
                .stock(50)
                .categoryCode("FOOD")
                .build());
        ProductResponseDto productB = productService.createProduct(ProductRequestDto.builder()
                .name("Comet Collar")
                .description("Sparkling collar")
                .price(BigDecimal.valueOf(30))
                .currency("USD")
                .stock(20)
                .categoryCode("GEAR")
                .build());
        return new Products(productA, productB);
    }
}
