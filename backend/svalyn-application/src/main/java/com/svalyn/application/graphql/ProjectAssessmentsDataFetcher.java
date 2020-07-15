/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Connection;
import com.svalyn.application.dto.output.Edge;
import com.svalyn.application.dto.output.PageInfo;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.AssessmentService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectAssessmentsDataFetcher implements DataFetcher<CompletableFuture<Connection<Assessment>>> {

    private static final String PAGE = "page";

    private final AssessmentService assessmentService;

    public ProjectAssessmentsDataFetcher(AssessmentService assessmentService) {
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public CompletableFuture<Connection<Assessment>> get(DataFetchingEnvironment environment) throws Exception {
        Project project = environment.getSource();

        Integer page = environment.getArgumentOrDefault(PAGE, 1);

        // @formatter:off
        Pageable pageable = PageRequest.of(page.intValue() - 1, 20);
        var assessmentCountMono = this.assessmentService.countByProjectId(project.getId());
        var assessmentEdgesMono = this.assessmentService.findAllByProjectId(project.getId(), pageable)
                .map(Edge::new)
                .collectList();
        // @formatter:on

        var connectionMono = assessmentCountMono.zipWith(assessmentEdgesMono, (assessmentCount, assessmentEdges) -> {
            return this.toConnection(pageable, assessmentCount, assessmentEdges);
        });

        return connectionMono.toFuture();
    }

    private Connection<Assessment> toConnection(Pageable pageable, Long count, List<Edge<Assessment>> edges) {
        boolean hasPreviousPage = pageable.hasPrevious();
        boolean hasNextPage = pageable.getOffset() + pageable.getPageSize() < count;
        int pageCount = (int) Math.ceil(count.doubleValue() / 20);

        var pageInfo = new PageInfo(hasPreviousPage, hasNextPage, pageCount);
        return new Connection<>(edges, pageInfo);
    }

}
