package com.aditya.project.repository;

import com.aditya.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select c from Cart c where c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("select c from Cart c where c.user.email = ?1 and c.cartId = ?2")
    Cart findCartByEmailAndUserId(String email, Long cartId);

    @Query("select c from Cart c join fetch c.cartItems ci join fetch ci.product p where p.id = ?1 ")
    List<Cart> findCartsByProductId(Long productId);
}
