/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;

@Service
public class ProjectRepository {

    private final List<Project> projects = new ArrayList<>();

    public int count() {
        return this.projects.size();
    }

    public List<Project> findAll(Pageable pageable) {
        int start = Long.valueOf(pageable.getOffset()).intValue();
        int end = start + pageable.getPageSize();

        if (start > this.projects.size()) {
            start = this.projects.size();
        }
        if (end > this.projects.size()) {
            end = this.projects.size();
        }

        return this.projects.subList(start, end);
    }

    public Optional<Project> findById(UUID projectId) {
        // @formatter:off
        return this.projects.stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
        // @formatter:on
    }

    public boolean existsByLabel(String label) {
        return this.projects.stream().anyMatch(project -> project.getLabel().equals(label));
    }

    public boolean existsById(UUID projectId) {
        return this.projects.stream().anyMatch(project -> project.getId().equals(projectId));
    }

    public Project createProject(UUID userId, String label) {
        Project project = new Project(UUID.randomUUID(), label, userId, LocalDateTime.now(ZoneOffset.UTC));
        this.projects.add(project);
        return project;
    }

    public void deleteProjects(List<UUID> projectIds) {
        // @formatter:off
        var projectsToRemove = this.projects.stream()
            .filter(project -> projectIds.contains(project.getId()))
            .collect(Collectors.toList());

        projectsToRemove.forEach(this.projects::remove);
        // @formatter:on
    }
}
