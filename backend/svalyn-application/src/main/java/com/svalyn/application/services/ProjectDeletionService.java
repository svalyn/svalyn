/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.DeleteProjectsInput;
import com.svalyn.application.dto.output.DeleteProjectsSuccessPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.ProjectRepository;

@Service
public class ProjectDeletionService {

    private final ProjectRepository projectRepository;

    private final AssessmentRepository assessmentRepository;

    public ProjectDeletionService(ProjectRepository projectRepository, AssessmentRepository assessmentRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload deleteProjects(DeleteProjectsInput input) {
        this.projectRepository.deleteProjects(input.getProjectIds());
        this.assessmentRepository.deleteAllByProjectIds(input.getProjectIds());
        return new DeleteProjectsSuccessPayload();
    }
}
