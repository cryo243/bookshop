package com.francis.bookshop.service;

import com.francis.bookshop.entity.Book;
import com.francis.bookshop.entity.Cart;
import com.francis.bookshop.entity.User;
import com.francis.bookshop.repository.BookRepository;
import com.francis.bookshop.repository.CartRepository;
import com.francis.bookshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Cart removeBookFromCart(String username, Long bookId) {
        Cart cart = getCartByUser(username);
        cart.getBooks().removeIf(book -> book.getId().equals(bookId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart addBookToCart(String username, Long bookId) {
        Cart cart = getCartByUser(username);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        cart.getBooks().add(book);
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return cartRepository.findByCustomer(user)
            .orElseGet(() -> cartRepository.save(new Cart(null, user, new ArrayList<>())));
    }

    @Transactional
    @Override
    public void checkout(String username) {
      Cart cart = getCartByUser(username);
      List<Book> books = cart.getBooks();

      books.forEach(book -> {
            if (book.getCopies() > 0) {
                book.setCopies(book.getCopies() - 1);
            }

      });
      bookRepository.saveAll(books);
      cartRepository.delete(cart);
    }

}
