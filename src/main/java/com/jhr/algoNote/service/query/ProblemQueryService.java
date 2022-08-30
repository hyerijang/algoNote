package com.jhr.algoNote.service.query;

import static java.util.stream.Collectors.toList;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.repository.query.ProblemQueryRepository;
import com.jhr.algoNote.repository.query.ProblemSearch;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemQueryService {

    private final ProblemQueryRepository problemQueryRepository;

    public List<Problem> search(int offset, int limit, ProblemSearch problemSearch) {
        return problemQueryRepository.search(offset,limit,problemSearch);
    }

    public List<ProblemDto> findAll(int offset, int limit) {
        List<Problem> problems = problemQueryRepository.findAll(offset, limit);
        return problems.stream().map(p -> new ProblemDto(p)).collect(toList());
    }

    @Getter
    static class ProblemDto {

        private Long problemId;
        private String problemTitle;
        private String problemSite;
        private String problemUrl;
        private String name;
        private String problemContent;
        //OneToMany
        private List<ProblemTagDto> problemTags;
        private List<ReviewDto> reviews;

        public ProblemDto(Problem problem) {
            this.problemId = problem.getId();
            this.problemTitle = problem.getTitle();
            this.problemSite = problem.getSite();
            this.problemUrl = problem.getUrl();
            this.name = problem.getMember().getName();
            this.problemContent = problem.getContent().getText();
            this.problemTags = problem.getProblemTags().stream()
                .map(pt -> new ProblemTagDto(pt.getTag().getName())).collect(toList());
            this.reviews = problem.getReviews().stream().map(r -> new ReviewDto(r.getTitle()))
                .collect(toList());

        }
    }

    @Getter
    @AllArgsConstructor
    static class ProblemTagDto {
        private String tagName;
    }

    @Getter
    @AllArgsConstructor
    static class ReviewDto {
        private String Title;
    }

}
