package com.toyboy.store.repository;

import com.toyboy.store.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

}
