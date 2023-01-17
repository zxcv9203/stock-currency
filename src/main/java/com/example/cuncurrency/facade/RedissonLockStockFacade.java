package com.example.cuncurrency.facade;

import com.example.cuncurrency.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * * Redisson을 이용한 pub/sub 구조의 동시성 해결 방법
 * ? 락 획득 재시도를 기본으로 제공합니다. (subscribe 하는 동안 기다림)
 * ? pub/sub 방식으로구현 되기 때문에 lettuce를 이용한 스핀락 방식보다 redis에 부하가 덜 갑니다.
 * ! 단, 별도의 라이브러리를 사용해야 합니다.
 * ! 그리고 lock을 라이브러리 차원에서 제공하기 때문에 사용법을 학습해야 합니다.
 * * 실무에서는 재시도가 필요한 경우에 Redisson을 사용합니다.
 */
@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;

    private final StockService stockService;


    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString());

        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decrease(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}