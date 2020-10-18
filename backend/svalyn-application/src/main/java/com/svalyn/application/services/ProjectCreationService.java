/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.ProjectRepository;

@Service
public class ProjectCreationService {

    private final ProjectRepository projectRepository;

    public ProjectCreationService(ProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    public IPayload createProject(UUID userId, CreateProjectInput input) {
        boolean exists = this.projectRepository.existsByLabel(input.getLabel());
        if (!exists) {
            ProjectEntity projectEntity = new ProjectEntity();
            projectEntity.setId(UUID.randomUUID());
            projectEntity.setLabel(input.getLabel());
            projectEntity.setCreatedBy(userId);
            projectEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));

            ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);
            Project project = new Project(savedProjectEntity.getId(), savedProjectEntity.getLabel(),
                    savedProjectEntity.getCreatedBy(), savedProjectEntity.getCreatedOn());

            return new CreateProjectSuccessPayload(project);
        }
        return new ErrorPayload("The project does already exist");
    }
}
