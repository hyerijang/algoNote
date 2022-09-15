package com.jhr.algoNote.repository.query;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.QProblem;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.QProblemTag;
import com.jhr.algoNote.domain.tag.QTag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemTagQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    QProblem problem = QProblem.problem;
    QTag qTag = QTag.tag;
    QProblemTag problemTag = QProblemTag.problemTag;

    public List<ProblemTag> findAllByProblemId(Long problemId) {

        return jpaQueryFactory
                .select(problemTag)
                .from(problemTag)
                .join(problemTag.tag, qTag)
                .fetchJoin()
                .fetch();
    }
}
