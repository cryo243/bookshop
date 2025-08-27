package com.francis.bookshop.service;

import com.francis.bookshop.entity.Cart;

public interface CartService {
    Cart removeBookFromCart(String username, Long bookId);
    Cart addBookToCart(String username, Long bookId);
    Cart getCartByUser(String username);
    void checkout(String username);
}
