package com.mikolajczyk.redude.backend.repository;

import com.mikolajczyk.redude.backend.domain.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface BookRepository extends CrudRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByGoogleId(String googleId);

    @Query
    List<Book> findAllByTitleAndAuthorAndCategory(@Param("TITLE") String title, @Param("AUTHOR") String author, @Param("CATEGORY") String categories);

    @Query
    List<Book> findAllByTitleAndAuthor(@Param("TITLE") String title, @Param("AUTHOR") String author);

    @Query
    List<Book> findAllByTitleAndCategory(@Param("TITLE") String title, @Param("CATEGORY") String categories);

    @Query
    List<Book> findAllByTitle(@Param("TITLE") String title);

    @Query
    List<Book> findAllByAuthorAndCategory(@Param("AUTHOR") String author, @Param("CATEGORY") String categories);

    @Query
    List<Book> findAllByAuthor(@Param("AUTHOR") String author);

    @Query
    List<Book> findAllByCategory(@Param("CATEGORY") String categories);

    @Query
    List<Book> findAllByQ(@Param("Q") String q);
}
