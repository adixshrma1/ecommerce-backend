package com.aditya.project.repository;

import com.aditya.project.model.Category;
import com.aditya.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
    Product findByProductName(String productName);
    Page<Product> findByCategory(Category category, Pageable pageDetails);
}
