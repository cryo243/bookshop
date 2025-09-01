package com.francis.bookshop.service;

import com.francis.bookshop.entity.Book;
import java.util.List;

public interface BookService {
  Book addBook(Book book);

  Book updateBook(Long id, Book updatedBook);

  void deleteBook(Long id);

  List<Book> findAllBooks();
}
