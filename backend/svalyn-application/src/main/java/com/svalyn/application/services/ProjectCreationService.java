/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
public class ProjectCreationService {

    private final IAccountRepository accountRepository;

    private final IProjectRepository projectRepository;

    private final ProjectConverter projectConverter;

    public ProjectCreationService(IAccountRepository accountRepository, IProjectRepository projectRepository,
            ProjectConverter projectConverter) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.projectConverter = Objects.requireNonNull(projectConverter);
    }

    public IPayload createProject(UUID userId, CreateProjectInput input) {
        IPayload payload = new ErrorPayload("An unexpected error has occurred");

        var optionalAccountEntity = this.accountRepository.findById(userId);
        boolean isValid = !input.getLabel().isBlank();
        boolean exists = this.projectRepository.existsByLabel(input.getLabel());
        if (!exists) {
            if (isValid && optionalAccountEntity.isPresent()) {
                AccountEntity accountEntity = optionalAccountEntity.get();

                ProjectEntity projectEntity = new ProjectEntity();
                projectEntity.setLabel(input.getLabel());
                projectEntity.setCreatedBy(accountEntity);
                projectEntity.setOwnedBy(accountEntity);
                projectEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                projectEntity.setMembers(new ArrayList<>());

                ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);

                Project project = this.projectConverter.convert(savedProjectEntity);
                payload = new CreateProjectSuccessPayload(project);
            } else {
                payload = new ErrorPayload("Invalid request");
            }
        } else {
            payload = new ErrorPayload("The project does already exist");
        }
        return payload;
    }

}
