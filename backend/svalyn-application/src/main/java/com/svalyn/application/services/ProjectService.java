/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.input.DeleteProjectsInput;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.DeleteProjectsSuccessPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.ProjectRepository;

import reactor.core.publisher.Mono;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final AssessmentRepository assessmentRepository;

    public ProjectService(ProjectRepository projectRepository, AssessmentRepository assessmentRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public Mono<IPayload> createProject(UUID userId, CreateProjectInput input) {
        // @formatter:off
        return this.projectRepository.createProject(userId, input.getLabel())
                .map(CreateProjectSuccessPayload::new)
                .filter(IPayload.class::isInstance)
                .map(IPayload.class::cast);
        // @formatter:on
    }

    public Mono<IPayload> deleteProjects(DeleteProjectsInput input) {
        // @formatter:off
        return this.projectRepository.deleteProjects(input.getProjectIds())
                .then(this.assessmentRepository.deleteAllByProjectIds(input.getProjectIds()))
                .then(Mono.just(new DeleteProjectsSuccessPayload()));
        // @formatter:on
    }

}
