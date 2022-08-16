package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.QMember;
import com.jhr.algoNote.domain.QProblem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public Long save(Problem problem) {
        em.persist(problem);
        return problem.getId();
    }

    public Problem findById(Long id) {
        return em.find(Problem.class, id);
    }

    // == QueryDSL== //
    QProblem problem = QProblem.problem;
    QMember member = QMember.member;

    /**
     * queryDsl을 이용하여 구현
     *
     * @param problemSearch
     * @return
     */
    public List<Problem> findAll(ProblemSearch problemSearch) {

        BooleanBuilder builder = new BooleanBuilder();

        // == 조회 ==
        return jpaQueryFactory
            .select(problem)
            .from(problem)
            .join(problem.member, member)
            .where(
                siteEq(problemSearch.getSite()),
                titleLike(problemSearch.getTitle()),
                emailEq(problemSearch.getMemberEmail()),
                contentTextLike(problemSearch.getContentText())
            )
            .limit(1000)
            .fetch();

    }

    // == 동적 쿼리 생성==
    private BooleanExpression titleLike(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return problem.title.contains(title);
    }

    private BooleanExpression siteEq(String site) {
        if (site == null || site.isBlank()) {
            return null;
        }
        return problem.site.eq(site);
    }

    private BooleanExpression emailEq(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return problem.member.email.eq(email);
    }

    private BooleanExpression contentTextLike(String contentText) {
        if (contentText == null || contentText.isBlank()) {
            return null;
        }
        return problem.content.text.contains(contentText);
    }

    public List<Problem> findAllWithFetchJoin(int offset, int limit) {
        return em.createQuery("select p from Problem p"
                + " join fetch p.member m"
                + " join fetch p.content c", Problem.class)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }
}
