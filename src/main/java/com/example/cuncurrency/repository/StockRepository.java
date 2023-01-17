package com.example.cuncurrency.repository;


import com.example.cuncurrency.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
