package com.francis.bookshop.service;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import com.francis.bookshop.entity.Book;
import com.francis.bookshop.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;

  @Override
  public Book addBook(Book book) {
    return bookRepository.save(book);
  }

  @Override
  public Book updateBook(Long id, Book updatedBook) {
    Book existingBook =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));

    if (nonNull(updatedBook.getTitle())) {
      existingBook.setTitle(updatedBook.getTitle());
    }
    if (nonNull(updatedBook.getAuthor())) {
      existingBook.setAuthor(updatedBook.getAuthor());
    }
    if (updatedBook.getYear() != 0) {
      existingBook.setYear(updatedBook.getYear());
    }
    if (updatedBook.getPrice() != 0.0) {
      existingBook.setPrice(updatedBook.getPrice());
    }
    if (updatedBook.getCopies() != 0) {
      existingBook.setCopies(updatedBook.getCopies());
    }
    return bookRepository.save(existingBook);
  }

  @Override
  public void deleteBook(Long id) {
    if (!bookRepository.existsById(id)) {
      throw new IllegalArgumentException("Book not found with ID: " + id);
    }
    bookRepository.deleteById(id);
  }

  @Override
  public List<Book> findAllBooks() {
    return bookRepository.findAll().stream().filter(book -> book.getCopies() > 0).collect(toList());
  }
}
