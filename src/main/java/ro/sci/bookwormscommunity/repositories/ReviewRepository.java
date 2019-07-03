package ro.sci.bookwormscommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.sci.bookwormscommunity.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Transactional
    @Query("SELECT r " +
            "FROM Review AS r " +
            "WHERE " +
            "r.book.id = :bookId " +
            "ORDER BY r.created")
    List<Review> getBookReviews(@Param("bookId")long bookId);
}