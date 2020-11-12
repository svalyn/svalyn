/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.IProjectRepository;

@Service
@Transactional(readOnly = true)
public class ProjectSearchService {

    private final IProjectRepository projectRepository;

    private final ProjectConverter projectConverter;

    public ProjectSearchService(IProjectRepository projectRepository, ProjectConverter projectConverter) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.projectConverter = Objects.requireNonNull(projectConverter);
    }

    public Optional<Project> findById(UUID userId, UUID projectId) {
        return this.projectRepository.findByUserIdAndProjectId(userId, projectId).map(this.projectConverter::convert);
    }

    public List<Project> findAll(UUID userId, Pageable pageable) {
        // @formatter:off
        return this.projectRepository.findAllByUserId(userId, pageable)
                .stream()
                .map(this.projectConverter::convert)
                .collect(Collectors.toList());
        // @formatter:on
    }

    public long count(UUID userId) {
        return this.projectRepository.countByUserId(userId);
    }
}
