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

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.ProjectRepository;

@Service
public class ProjectSearchService {

    private final ProjectRepository projectRepository;

    public ProjectSearchService(ProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    public Optional<Project> findById(UUID projectId) {
        return this.projectRepository.findById(projectId).map(this::convert);
    }

    public List<Project> findAll(Pageable pageable) {
        // @formatter:off
        return this.projectRepository.findAll(pageable)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
        // @formatter:on
    }

    public Project convert(ProjectEntity projectEntity) {
        return new Project(projectEntity.getId(), projectEntity.getLabel(), projectEntity.getCreatedBy(),
                projectEntity.getCreatedOn());
    }

    public int count() {
        return this.projectRepository.count();
    }
}
