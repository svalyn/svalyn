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

import com.svalyn.application.dto.output.Connection;
import com.svalyn.application.dto.output.Edge;
import com.svalyn.application.dto.output.PageInfo;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.ProjectRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class QueryProjectsDataFetcher implements DataFetcher<Connection<Project>> {

    private static final String PAGE = "page";

    private final ProjectRepository projectRepository;

    public QueryProjectsDataFetcher(ProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    @Override
    public Connection<Project> get(DataFetchingEnvironment environment) throws Exception {
        int page = environment.getArgumentOrDefault(PAGE, 0).intValue();
        if (page < 0) {
            page = 0;
        }

        // @formatter:off
        Pageable pageable = PageRequest.of(page, 20);
        var projectCount = this.projectRepository.count();
        var projectEdges = this.projectRepository.findAll(pageable).stream()
                .map(Edge::new)
                .collect(Collectors.toList());
        // @formatter:on

        return this.toConnection(pageable, projectCount, projectEdges);
    }

    private Connection<Project> toConnection(Pageable pageable, int count, List<Edge<Project>> edges) {
        boolean hasPreviousPage = pageable.hasPrevious();
        boolean hasNextPage = pageable.getOffset() + pageable.getPageSize() < count;
        var pageInfo = new PageInfo(hasPreviousPage, hasNextPage, count);
        return new Connection<>(edges, pageInfo);
    }

}
