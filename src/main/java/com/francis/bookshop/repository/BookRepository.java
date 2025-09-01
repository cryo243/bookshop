package com.francis.bookshop.repository;

import com.francis.bookshop.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {}
