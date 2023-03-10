package com.example.cuncurrency.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.facade.OptimisticLockStockFacade;
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
class StockService3Test {
    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;
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
    @DisplayName("동시에 100개의 요청이 들어오는 요청")
    void stock_decrease_100_request() throws InterruptedException {
        // given
        Long want = 0L;
        Long decreaseCount = 1L;
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);
        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(stock.getId(), decreaseCount);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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