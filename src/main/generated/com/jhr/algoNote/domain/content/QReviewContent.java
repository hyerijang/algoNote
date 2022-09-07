package com.jhr.algoNote.domain.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewContent is a Querydsl query type for ReviewContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewContent extends EntityPathBase<ReviewContent> {

    private static final long serialVersionUID = 1992427588L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewContent reviewContent = new QReviewContent("reviewContent");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.jhr.algoNote.domain.QReview review;

    public final StringPath text = createString("text");

    public QReviewContent(String variable) {
        this(ReviewContent.class, forVariable(variable), INITS);
    }

    public QReviewContent(Path<? extends ReviewContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewContent(PathMetadata metadata, PathInits inits) {
        this(ReviewContent.class, metadata, inits);
    }

    public QReviewContent(Class<? extends ReviewContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new com.jhr.algoNote.domain.QReview(forProperty("review"), inits.get("review")) : null;
    }

}

