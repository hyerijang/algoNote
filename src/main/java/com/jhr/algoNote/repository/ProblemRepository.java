package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.Problem;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemRepository {

    private final EntityManager em;

    public Long save(Problem problem) {
        em.persist(problem);
        return problem.getId();
    }

    public Problem findById(Long id) {
        return em.find(Problem.class, id);
    }


}
