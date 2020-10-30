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

import com.svalyn.application.dto.input.CreateAssessmentInput;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.CreateAssessmentSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IAssessmentRepository;
import com.svalyn.application.repositories.IDescriptionRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
public class AssessmentCreationService {

    private final AssessmentConverter assessmentConverter;

    private final IAccountRepository accountRepository;

    private final IProjectRepository projectRepository;

    private final IDescriptionRepository descriptionRepository;

    private final IAssessmentRepository assessmentRepository;

    public AssessmentCreationService(AssessmentConverter assessmentConverter, IAccountRepository accountRepository,
            IProjectRepository projectRepository, IDescriptionRepository descriptionRepository,
            IAssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload createAssessment(UUID userId, CreateAssessmentInput input) {
        IPayload payload = new ErrorPayload("An unexpected error has occurred");

        var opttionalAccountEntity = this.accountRepository.findById(userId);
        var optionalProjectEntity = this.projectRepository.findById(input.getProjectId());
        var optionalDescriptionEntity = this.descriptionRepository.findById(input.getDescriptionId());
        boolean isValid = !input.getLabel().isBlank();

        if (optionalProjectEntity.isPresent()) {
            if (isValid && opttionalAccountEntity.isPresent() && optionalDescriptionEntity.isPresent()) {
                AccountEntity accountEntity = opttionalAccountEntity.get();
                ProjectEntity projectEntity = optionalProjectEntity.get();
                DescriptionEntity descriptionEntity = optionalDescriptionEntity.get();

                AssessmentEntity assessmentEntity = new AssessmentEntity();
                assessmentEntity.setDescription(descriptionEntity);
                assessmentEntity.setProject(projectEntity);
                assessmentEntity.setLabel(input.getLabel());
                assessmentEntity.setResults(new ArrayList<>());
                assessmentEntity.setCreatedBy(accountEntity);
                assessmentEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setLastModifiedBy(accountEntity);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

                AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                Assessment assessment = this.assessmentConverter.convert(savedAssessmentEntity);

                payload = new CreateAssessmentSuccessPayload(assessment);
            } else {
                payload = new ErrorPayload("Invalid request");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }
        return payload;
    }

}
