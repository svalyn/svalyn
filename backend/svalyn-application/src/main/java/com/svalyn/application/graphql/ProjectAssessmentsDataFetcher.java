/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Connection;
import com.svalyn.application.dto.output.Edge;
import com.svalyn.application.dto.output.PageInfo;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.AssessmentSearchService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectAssessmentsDataFetcher implements DataFetcher<Connection<Assessment>> {

    private static final String PAGE = "page";

    private final AssessmentSearchService assessmentSearchService;

    public ProjectAssessmentsDataFetcher(AssessmentSearchService assessmentSearchService) {
        this.assessmentSearchService = Objects.requireNonNull(assessmentSearchService);
    }

    @Override
    public Connection<Assessment> get(DataFetchingEnvironment environment) throws Exception {
        Project project = environment.getSource();

        int page = environment.getArgumentOrDefault(PAGE, 0).intValue();
        if (page < 0) {
            page = 0;
        }

        // @formatter:off
        Pageable pageable = PageRequest.of(page, 20);
        var assessmentCount = this.assessmentSearchService.countByProjectId(project.getId());
        var assessmentEdges = this.assessmentSearchService.findAllByProjectId(project.getId(), pageable).stream()
                .map(Edge::new)
                .collect(Collectors.toList());
        // @formatter:on
        return this.toConnection(pageable, assessmentCount, assessmentEdges);
    }

    private Connection<Assessment> toConnection(Pageable pageable, int count, List<Edge<Assessment>> edges) {
        boolean hasPreviousPage = pageable.hasPrevious();
        boolean hasNextPage = pageable.getOffset() + pageable.getPageSize() < count;

        var pageInfo = new PageInfo(hasPreviousPage, hasNextPage, count);
        return new Connection<>(edges, pageInfo);
    }

}
