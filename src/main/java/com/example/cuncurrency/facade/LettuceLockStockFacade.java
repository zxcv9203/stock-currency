package com.example.cuncurrency.facade;

import com.example.cuncurrency.repository.RedisRepository;
import com.example.cuncurrency.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * * Lettuce를 이용한 스핀락 방식의 락 구현
 * ? spirng data redis를 사용하면 lettuce가 기본이기 때문에 사용하기 편리합니다.
 * ! 스핀락 방식이기 때문에 동시에 많은 쓰레드가 lock 대기 상태라면 redis에 부하가 갈 수 있습니다.
 * ? 재시도가 필요하지 않은 lock은 lettuce를 활용합니다.
 */
@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

    private final RedisRepository redisRepository;

    private final StockService stockService;

    public void decrease(Long key, Long quantity) throws InterruptedException {
        // ! lock을 획득할때까지 스핀락 방식으로 무한루프 발생
        // ! 스핀락 방식이기 때문에 redis의 부하가 갈 수 있으므로 sleep 등을 이용해 너무 많은 요청을 하지 않도록 제어 해주어야 합니다.
        while (!redisRepository.lock(key)) {
            Thread.sleep(100);
        }
        try {
            stockService.decrease(key, quantity);
        } finally {
            redisRepository.unlock(key);
        }
    }
}
