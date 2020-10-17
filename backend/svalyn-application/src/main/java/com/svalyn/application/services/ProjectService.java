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
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final AssessmentRepository assessmentRepository;

    public ProjectService(ProjectRepository projectRepository, AssessmentRepository assessmentRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload createProject(UUID userId, CreateProjectInput input) {
        boolean exists = this.projectRepository.existsByLabel(input.getLabel());
        if (!exists) {
            Project project = this.projectRepository.createProject(userId, input.getLabel());
            return new CreateProjectSuccessPayload(project);
        }
        return new ErrorPayload("The project does already exist");
    }

    public IPayload deleteProjects(DeleteProjectsInput input) {
        this.projectRepository.deleteProjects(input.getProjectIds());
        this.assessmentRepository.deleteAllByProjectIds(input.getProjectIds());
        return new DeleteProjectsSuccessPayload();
    }

}
