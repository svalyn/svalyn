/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.input.DeleteProjectInput;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.DeleteProjectSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
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

    public Mono<IPayload> createProject(CreateProjectInput input) {
        return this.projectRepository.createProject(input.getLabel()).map(CreateProjectSuccessPayload::new)
                .filter(IPayload.class::isInstance).map(IPayload.class::cast);
    }

    public Mono<IPayload> deleteProject(DeleteProjectInput input) {
        return this.projectRepository.existById(input.getProjectId()).flatMap(existById -> {
            if (existById.booleanValue()) {
                // @formatter:off
                return this.projectRepository.deleteProject(input.getProjectId())
                        .then(this.assessmentRepository.deleteAllByProjectId(input.getProjectId()))
                        .then(Mono.just(new DeleteProjectSuccessPayload()));
                // @formatter:on
            }
            return Mono.just(new ErrorPayload("The project does not exist"));
        }).filter(IPayload.class::isInstance).map(IPayload.class::cast);
    }

}
