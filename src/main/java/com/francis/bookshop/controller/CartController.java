package com.francis.bookshop.controller;

import com.francis.bookshop.dto.CheckoutDto;
import com.francis.bookshop.entity.Cart;
import com.francis.bookshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.getCartByUser(username));
    }

    @PostMapping("/add/{bookId}")
    public ResponseEntity<Cart> addToCart(@PathVariable Long bookId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.addBookToCart(username, bookId));
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long bookId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.removeBookFromCart(username, bookId));
    }
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(Authentication authentication) {
        String username = authentication.getName();
        cartService.checkout(username);
        return ResponseEntity.ok("Order confirmed");
    }
}