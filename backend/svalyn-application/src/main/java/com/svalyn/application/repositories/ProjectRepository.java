/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjectRepository {

    private final List<Project> projects = List.of(
            new Project(UUID.nameUUIDFromBytes("Westworld".getBytes()), "Westworld"),
            new Project(UUID.nameUUIDFromBytes("Eastworld".getBytes()), "Eastworld"),
            new Project(UUID.nameUUIDFromBytes("Jurassic World".getBytes()), "Jurassic World"));

    public Flux<Project> findAll() {
        return Flux.fromIterable(this.projects);
    }

    public Mono<Project> findById(UUID projectId) {
        // @formatter:off
        var optionalProject = this.projects.stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalProject);
    }

    public boolean existById(UUID projectId) {
        return this.projects.stream().anyMatch(project -> project.getId().equals(projectId));
    }
}
