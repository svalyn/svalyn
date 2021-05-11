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
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.output.Connection;
import com.svalyn.application.dto.output.Edge;
import com.svalyn.application.dto.output.PageInfo;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.ProjectSearchService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class QueryProjectsDataFetcher {

    private final UserDetailsService userDetailsService;

    private final ProjectSearchService projectSearchService;

    public QueryProjectsDataFetcher(UserDetailsService userDetailsService, ProjectSearchService projectSearchService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.projectSearchService = Objects.requireNonNull(projectSearchService);
    }

    @DgsData(parentType = "Query", field = "projects")
    public Connection<Project> get(@InputArgument("page") int page) {
        int sanitizedPage = page;
        if (sanitizedPage < 0) {
            sanitizedPage = 0;
        }

        var userDetails = this.userDetailsService.getUserDetails();

        // @formatter:off
        Pageable pageable = PageRequest.of(sanitizedPage, 20, Sort.by(Direction.ASC, "label"));
        var projectCount = this.projectSearchService.count(userDetails.getId());
        var projectEdges = this.projectSearchService.findAll(userDetails.getId(), pageable).stream()
                .map(Edge::new)
                .collect(Collectors.toList());
        // @formatter:on

        return this.toConnection(pageable, projectCount, projectEdges);
    }

    private Connection<Project> toConnection(Pageable pageable, long count, List<Edge<Project>> edges) {
        boolean hasPreviousPage = pageable.hasPrevious();
        boolean hasNextPage = pageable.getOffset() + pageable.getPageSize() < count;
        var pageInfo = new PageInfo(hasPreviousPage, hasNextPage, count);
        return new Connection<>(edges, pageInfo);
    }

}
