package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.tag.ProblemTag;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemTagRepository {

    private final EntityManager em;


    public void deleteAllByProblemId(Problem problem) {
        String jpql = "DELETE FROM ProblemTag where problem.id = :problemid";
        Query query = em.createQuery(jpql).setParameter("problemid", problem.getId());
        query.executeUpdate(); //벌크 연산 :데이터베이스에 직접 쿼리 (엔티티에 반영 X)
        problem.getProblemTags().clear(); //엔티티 clear
    }
}
