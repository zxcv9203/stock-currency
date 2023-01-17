package com.example.cuncurrency.facade;

import com.example.cuncurrency.service.StockService3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final StockService3 stockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decrease(id,quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
