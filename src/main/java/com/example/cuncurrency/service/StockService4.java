package com.example.cuncurrency.service;

import com.example.cuncurrency.domain.stock.Stock;
import com.example.cuncurrency.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * * DB를 이용한 동시성 문제 해결 - Named Lock
 * ? 이름을 가진 metadata locking 입니다.
 * ? 이름을 가진 lock을 획득한 후 해제할 때 까지 다른 세션은 이 lock을 획득할 수 없도록 합니다.
 * ? 주의할 점으로는 transaction이 종료될 때 lock이 자동으로 해제되지 않습니다.
 * ? 별도의 명령어로 해제를 수행해주거나 선점시간이 끝나야 해제됩니다.
 * ! 실무에서는 별도의 datasource를 사용하여 lock 전용 DB를 사용하는 것을 추천 (다른 서비스의 커넥션 풀이 부족해져서 문제 발생할 위험이 있기 때문)
 * * 타임아웃을 구현하기 쉽고 분산락을 구현할때 많이 사용합니다.
 * * 락해제와 세션관리를 해야하기 때문에 관리를 잘해야 하므로 실제로 구현할때는 복잡할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class StockService4 {

    private final StockRepository stockRepository;

    // ! 부모와 별도의 트랜잭션으로 관리하기 위해 해당 어노테이션 추가
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        // get stock
        Stock stock = stockRepository.findById(id)
            .orElseThrow();

        // 재고감소
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);

    }
}
