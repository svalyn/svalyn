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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Connection;
import com.svalyn.application.dto.output.Edge;
import com.svalyn.application.dto.output.PageInfo;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.AssessmentSearchService;

@DgsComponent
public class ProjectAssessmentsDataFetcher {

    private final AssessmentSearchService assessmentSearchService;

    public ProjectAssessmentsDataFetcher(AssessmentSearchService assessmentSearchService) {
        this.assessmentSearchService = Objects.requireNonNull(assessmentSearchService);
    }

    @DgsData(parentType = "Project", field = "assessments")
    public Connection<Assessment> get(DgsDataFetchingEnvironment environment, @InputArgument("page") int page) {
        Project project = environment.getSource();

        int sanitizedPage = page;
        if (sanitizedPage < 0) {
            sanitizedPage = 0;
        }

        // @formatter:off
        Pageable pageable = PageRequest.of(sanitizedPage, 20, Sort.by(Direction.DESC, "createdOn"));
        var assessmentCount = this.assessmentSearchService.countByProjectId(project.getId());
        var assessmentEdges = this.assessmentSearchService.findAllByProjectId(project.getId(), pageable).stream()
                .map(Edge::new)
                .collect(Collectors.toList());
        // @formatter:on
        return this.toConnection(pageable, assessmentCount, assessmentEdges);
    }

    private Connection<Assessment> toConnection(Pageable pageable, long count, List<Edge<Assessment>> edges) {
        boolean hasPreviousPage = pageable.hasPrevious();
        boolean hasNextPage = pageable.getOffset() + pageable.getPageSize() < count;

        var pageInfo = new PageInfo(hasPreviousPage, hasNextPage, count);
        return new Connection<>(edges, pageInfo);
    }

}
