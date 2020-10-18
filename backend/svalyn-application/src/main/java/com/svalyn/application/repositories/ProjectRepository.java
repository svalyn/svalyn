/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.entities.ProjectEntity;

@Service
public class ProjectRepository {

    private final List<ProjectEntity> projectEntities = new ArrayList<>();

    public int count() {
        return this.projectEntities.size();
    }

    public List<ProjectEntity> findAll(Pageable pageable) {
        int start = Long.valueOf(pageable.getOffset()).intValue();
        int end = start + pageable.getPageSize();

        if (start > this.projectEntities.size()) {
            start = this.projectEntities.size();
        }
        if (end > this.projectEntities.size()) {
            end = this.projectEntities.size();
        }

        return this.projectEntities.subList(start, end);
    }

    public Optional<ProjectEntity> findById(UUID projectId) {
        // @formatter:off
        return this.projectEntities.stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
        // @formatter:on
    }

    public boolean existsByLabel(String label) {
        return this.projectEntities.stream().anyMatch(project -> project.getLabel().equals(label));
    }

    public boolean existsById(UUID projectId) {
        return this.projectEntities.stream().anyMatch(project -> project.getId().equals(projectId));
    }

    public ProjectEntity save(ProjectEntity projectEntity) {
        this.projectEntities.add(projectEntity);
        return projectEntity;
    }

    public void deleteProjects(List<UUID> projectIds) {
        // @formatter:off
        var projectsToRemove = this.projectEntities.stream()
            .filter(project -> projectIds.contains(project.getId()))
            .collect(Collectors.toList());

        projectsToRemove.forEach(this.projectEntities::remove);
        // @formatter:on
    }
}
