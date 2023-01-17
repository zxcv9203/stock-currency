package com.example.cuncurrency.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StockServiceTest {
    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    private Stock stock;

    @BeforeEach
    public void before() {
        stock = Stock.builder()
            .productId(1L)
            .quantity(100L)
            .build();

        stock = stockRepository.saveAndFlush(stock);
    }

    @Test
    @DisplayName("재고 감소가 잘되는지 테스트")
    void stock_decrease() {
        //given
        Long decreaseCount = 1L;
        Long want = 99L;

        // when
        stockService.decrease(stock.getId(), decreaseCount);
        Stock got = stockRepository.findById(stock.getId())
            .orElseThrow();

        // then
        assertThat(got.getQuantity()).isEqualTo(want);
    }
}