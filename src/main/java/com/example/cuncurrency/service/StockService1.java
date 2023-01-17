package com.example.cuncurrency.service;

import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ! 자바의 syncronized 구문은 하나의 프로세스 일때만 동시성 보장
 */
@Service
@RequiredArgsConstructor
//@Transactional
public class StockService1 {

    private final StockRepository stockRepository;

    /**
     * ! syncronized를 붙여도 에러가 나는 이유는 스프링의 @Transactional 동작 방식 때문
     * ? proxy 방식으로 동작하기 때문에 thread-safe 하지 않음
     * * 현재는 @Transactional을 제거함으로써 해결
     * @param id
     * @param quantity
     */
    public synchronized void decrease(Long id, Long quantity) {
        // get stock
        Stock stock = stockRepository.findById(id)
            .orElseThrow();

        // 재고감소
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);

    }
}
