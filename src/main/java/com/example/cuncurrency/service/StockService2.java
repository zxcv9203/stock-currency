package com.example.cuncurrency.service;

import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * * DB를 이용한 동시성 문제 해결 - Pessimistic Lock
 * ? 실제로 락을 걸어서 정합성을 맞추는 방법 exclusive lock(쓰기 잠금)을 걸게 되면 다른 트랜잭션에서는 lock이 해제되기전에
 * ? 데이터를 가져갈 수 없습니다.
 * ? 데드락이 걸릴 수 있기 때문에 주의해서 사용해야 합니다.
 * * 충돌이 일어난다면 Optimistic Lock보다 성능이 좋을 수 있습니다.
 * * 데이터 정합성이 어느정도 보장될 수 있습니다.
 * * 별도의 Lock을 잡기때문에 성능 저하가 발생합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockService2 {

    private final StockRepository stockRepository;

    /**
     * @param id
     * @param quantity
     */
    public void decrease(Long id, Long quantity) {
        // get stock
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);

        // 재고감소
        stock.decrease(quantity);

        // 저장
        stockRepository.saveAndFlush(stock);

    }
}
