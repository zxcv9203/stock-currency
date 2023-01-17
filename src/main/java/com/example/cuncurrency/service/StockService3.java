package com.example.cuncurrency.service;

import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * * DB를 이용한 동시성 문제 해결 - Optimistic Lock
 * ? 실제로 Lock을 이용하지 않고 버전을 이용함으로써 정합성을 맞추는 방법입니다.
 * ? 먼저, 데이터를 읽은 후에 update를 수행할 때 현재 내가 읽은 버전이 맞는지 확인한 후 업데이트 합니다.
 * ? 내가 읽은 버전에서 수정 사항이 생겼을 경우에는 application에서 다시 읽은 후에 작업을 수행합니다.
 * ? 별도의 Lock을 잡지 않아도 되기 때문에 충돌이 많지 않다면 성능상 이점이 있을 수 있다.
 * ? 개발자가 재시도 로직을 직접 작성해주어야 한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockService3 {

    private final StockRepository stockRepository;

    /**
     * @param id
     * @param quantity
     */
    public synchronized void decrease(Long id, Long quantity) {
        // get stock
        Stock stock = stockRepository.findByIdOptimisticLock(id);

        // 재고감소
        stock.decrease(quantity);

        // 저장
        stockRepository.saveAndFlush(stock);

    }
}
