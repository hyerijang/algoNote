package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.Review;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewTagRepository {
    private final EntityManager em;

    public void deleteAllByReviewId(Long reviewId) {
        String jpql="DELETE FROM ReviewTag where review.id = :reviewId";
        Query query = em.createQuery(jpql).setParameter("reviewId", reviewId);
        query.executeUpdate();
    }
}
