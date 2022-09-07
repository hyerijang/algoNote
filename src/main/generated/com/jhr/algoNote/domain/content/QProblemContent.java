package com.jhr.algoNote.domain.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProblemContent is a Querydsl query type for ProblemContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProblemContent extends EntityPathBase<ProblemContent> {

    private static final long serialVersionUID = -1471755529L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProblemContent problemContent = new QProblemContent("problemContent");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.jhr.algoNote.domain.QProblem problem;

    public final StringPath text = createString("text");

    public QProblemContent(String variable) {
        this(ProblemContent.class, forVariable(variable), INITS);
    }

    public QProblemContent(Path<? extends ProblemContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProblemContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProblemContent(PathMetadata metadata, PathInits inits) {
        this(ProblemContent.class, metadata, inits);
    }

    public QProblemContent(Class<? extends ProblemContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.problem = inits.isInitialized("problem") ? new com.jhr.algoNote.domain.QProblem(forProperty("problem"), inits.get("problem")) : null;
    }

}

