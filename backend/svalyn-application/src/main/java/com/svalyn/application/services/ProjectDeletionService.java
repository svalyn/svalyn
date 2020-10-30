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
import com.svalyn.application.repositories.IProjectRepository;

@Service
public class ProjectDeletionService {

    private final IProjectRepository projectRepository;

    public ProjectDeletionService(IProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    public IPayload deleteProjects(DeleteProjectsInput input) {
        this.projectRepository.deleteWithId(input.getProjectIds());
        return new DeleteProjectsSuccessPayload();
    }
}
