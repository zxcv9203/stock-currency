package com.example.cuncurrency.facade;

import com.example.cuncurrency.repository.LockRepository;
import com.example.cuncurrency.service.StockService4;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService4 stockService4;

    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService4.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
