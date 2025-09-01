package com.francis.bookshop.controller;

import com.francis.bookshop.entity.Book;
import com.francis.bookshop.service.AuthService;
import com.francis.bookshop.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;
  private final AuthService authService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Book> addBook(@RequestBody Book book, Authentication auth) {
    authService.ensureAdmin(auth.getName());
    return ResponseEntity.ok(bookService.addBook(book));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Book> updateBook(
      @PathVariable Long id, @RequestBody Book book, Authentication auth) {
    authService.ensureAdmin(auth.getName());
    return ResponseEntity.ok(bookService.updateBook(id, book));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication auth) {
    authService.ensureAdmin(auth.getName());
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.findAllBooks());
  }
}
