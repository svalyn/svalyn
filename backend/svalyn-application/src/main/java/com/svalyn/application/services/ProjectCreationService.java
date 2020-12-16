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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
@Transactional
public class ProjectCreationService {

    private final IAccountRepository accountRepository;

    private final IProjectRepository projectRepository;

    private final DefaultDescriptionProvider defaultDescriptionProvider;

    private final DescriptionConverter descriptionConverter;

    private final ProjectConverter projectConverter;

    public ProjectCreationService(IAccountRepository accountRepository, IProjectRepository projectRepository,
            DefaultDescriptionProvider defaultDescriptionProvider, DescriptionConverter descriptionConverter,
            ProjectConverter projectConverter) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.defaultDescriptionProvider = Objects.requireNonNull(defaultDescriptionProvider);
        this.descriptionConverter = Objects.requireNonNull(descriptionConverter);
        this.projectConverter = Objects.requireNonNull(projectConverter);
    }

    public IPayload createProject(UUID userId, CreateProjectInput input) {
        IPayload payload = new ErrorPayload("An unexpected error has occurred");

        var optionalAccountEntity = this.accountRepository.findById(userId);
        boolean isValid = !input.getLabel().isBlank();
        boolean exists = this.projectRepository.existsByUserIdAndLabel(userId, input.getLabel());
        if (!exists) {
            if (isValid && optionalAccountEntity.isPresent()) {
                AccountEntity accountEntity = optionalAccountEntity.get();

                // @formatter:off
                List<DescriptionEntity> descriptions = this.defaultDescriptionProvider.getDescriptions().stream()
                        .map(this.descriptionConverter::convertToEntity)
                        .collect(Collectors.toList());
                // @formatter:on

                ProjectEntity projectEntity = new ProjectEntity();
                projectEntity.setLabel(input.getLabel());
                projectEntity.setOwnedBy(accountEntity);
                projectEntity.setCreatedBy(accountEntity);
                projectEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                projectEntity.setDescriptions(descriptions);
                projectEntity.setMembers(new ArrayList<>());

                descriptions.forEach(descriptionEntity -> descriptionEntity.setProject(projectEntity));

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
