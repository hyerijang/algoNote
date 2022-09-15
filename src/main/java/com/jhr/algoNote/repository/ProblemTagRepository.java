package com.jhr.algoNote.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.jhr.algoNote.domain.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemTagRepository {

    private final EntityManager em;


    /**
     * 벌크연산, 특정 problem entity의 ProblemTag를 지연로딩하는 것 보다 우선되어야함
     * @param problemId
     */
    public void deleteAllByProblemId(Long problemId) {
        String jpql = "DELETE FROM ProblemTag where problem.id = :problemid";
        Query query = em.createQuery(jpql).setParameter("problemid", problemId);
        query.executeUpdate(); //벌크 연산 :데이터베이스에 직접 쿼리 (엔티티에 반영 X)
    }
}
