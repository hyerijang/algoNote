package com.jhr.algoNote.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -917518496L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final com.jhr.algoNote.domain.content.QReviewContent content;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final QProblem problem;

    public final ListPath<com.jhr.algoNote.domain.tag.ReviewTag, com.jhr.algoNote.domain.tag.QReviewTag> reviewTags = this.<com.jhr.algoNote.domain.tag.ReviewTag, com.jhr.algoNote.domain.tag.QReviewTag>createList("reviewTags", com.jhr.algoNote.domain.tag.ReviewTag.class, com.jhr.algoNote.domain.tag.QReviewTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new com.jhr.algoNote.domain.content.QReviewContent(forProperty("content"), inits.get("content")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.problem = inits.isInitialized("problem") ? new QProblem(forProperty("problem"), inits.get("problem")) : null;
    }

}

