package com.example.cuncurrency.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockService2Test {
    @Autowired
    private StockService2 stockService;

    @Autowired
    private StockRepository stockRepository;

    private Stock stock;

    @BeforeEach
    void before() {
        stock = Stock.builder()
            .productId(1L)
            .quantity(100L)
            .build();

        stock = stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void after() {
        stockRepository.deleteAllInBatch();
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

    @Test
    @DisplayName("동시에 100개의 요청이 들어오는 요청")
    void stock_decrease_100_request() throws InterruptedException {
        // given
        Long want = 0L;
        Long decreaseCount = 1L;
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);
        System.out.println(stock.getId());
        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(stock.getId(), decreaseCount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock got = stockRepository.findById(stock.getId())
            .orElseThrow();

        // then
        assertThat(got.getQuantity()).isEqualTo(want);
    }
}