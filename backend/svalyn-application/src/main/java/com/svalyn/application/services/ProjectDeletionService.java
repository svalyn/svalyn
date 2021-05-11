/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.input.DeleteProjectsInput;
import com.svalyn.application.dto.output.DeleteProjectsSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.repositories.IProjectRepository;

@Service
@Transactional
public class ProjectDeletionService {

    private final IProjectRepository projectRepository;

    public ProjectDeletionService(IProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    public IPayload deleteProjects(UserDetails userDetails, DeleteProjectsInput input) {
        IPayload payload = new DeleteProjectsSuccessPayload(userDetails);
        if (!input.getProjectIds().isEmpty()) {
            if (!this.projectRepository.ownsAllByIds(userDetails.getId(), input.getProjectIds())) {
                payload = new ErrorPayload("Projects can only be deleted by their owner");
            } else {
                this.projectRepository.deleteByUserIdAndProjectIds(userDetails.getId(), input.getProjectIds());
                payload = new DeleteProjectsSuccessPayload(userDetails);
            }
        }

        return payload;
    }
}
