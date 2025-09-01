package com.francis.bookshop.repository;

import com.francis.bookshop.entity.Cart;
import com.francis.bookshop.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByCustomer(User user);
}
