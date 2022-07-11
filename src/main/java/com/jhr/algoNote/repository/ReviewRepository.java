package com.jhr.algoNote.repository;


import com.jhr.algoNote.domain.Review;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewRepository {

    private final EntityManager em;

    public Long save(Review review) {
        em.persist(review);
        return review.getId();
    }

    public Review findOne(Long id) {
        return em.find(Review.class, id);

    }
}
