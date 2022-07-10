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

    public void deleteAllByProblemId(Long problemID) {
        String jpql="DELETE FROM ProblemTag where problem.id = :problemid";
        Query query = em.createQuery(jpql).setParameter("problemid", problemID);
        query.executeUpdate();
    }
}
