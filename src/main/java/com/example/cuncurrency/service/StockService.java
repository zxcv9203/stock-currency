package com.example.cuncurrency.service;

import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    public void decrease(Long id, Long quantity) {
        // get stock
        Stock stock = stockRepository.findById(id)
            .orElseThrow();

        // 재고감소
        stock.decrease(quantity);

    }
}
