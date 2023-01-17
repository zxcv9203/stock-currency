package com.example.cuncurrency.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;

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

    /**
     * ! Race Condition 발생
     * ? ExecutorService에 대해서
     * ? CountDownLatch에 대해서
     */
    @Test
    @DisplayName("동시에 100개의 요청이 들어오는 요청")
    void stock_decrease_100_request() throws InterruptedException {
        // given
        Long want = 0L;
        Long decreaseCount = 1L;
        int threadCount = 100;
        // ? 100개의 요청을 날리기 위해 사용
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // ? 100개의 요청이 끝날때까지 기다리는 용도
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