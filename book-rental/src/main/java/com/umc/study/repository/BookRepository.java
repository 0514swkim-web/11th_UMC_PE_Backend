package com.umc.study.repository;

import com.umc.study.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// SQL 문자열 없이 메서드 이름으로 쿼리를 만들어 주는 Spring Data JPA Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // category를 함께 JOIN해서 가져와 N+1 쿼리를 막음
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByOrderByBookIdDesc();

    @EntityGraph(attributePaths = "category")
    List<Book> findAllByCategory_CategoryIdOrderByBookIdDesc(Long categoryId);

    // WHERE title LIKE '%keyword%'
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByTitleContainingOrderByBookIdDesc(String keyword);
}
