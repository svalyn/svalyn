/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateAssessmentInput;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.CreateAssessmentSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.DescriptionRepository;
import com.svalyn.application.repositories.ProjectRepository;

@Service
public class AssessmentCreationService {

    private final AssessmentConverter assessmentConverter;

    private final ProjectRepository projectRepository;

    private final DescriptionRepository descriptionRepository;

    private final AssessmentRepository assessmentRepository;

    public AssessmentCreationService(AssessmentConverter assessmentConverter, ProjectRepository projectRepository,
            DescriptionRepository descriptionRepository, AssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload createAssessment(UUID userId, CreateAssessmentInput input) {
        boolean exists = this.projectRepository.existsById(input.getProjectId());
        if (exists) {
            Optional<DescriptionEntity> optionalDescriptionEntity = this.descriptionRepository
                    .findDescriptionById(input.getDescriptionId());
            if (optionalDescriptionEntity.isPresent()) {
                DescriptionEntity descriptionEntity = optionalDescriptionEntity.get();

                AssessmentEntity assessmentEntity = new AssessmentEntity();
                assessmentEntity.setId(UUID.randomUUID());
                assessmentEntity.setDescriptionId(descriptionEntity.getId());
                assessmentEntity.setProjectId(input.getProjectId());
                assessmentEntity.setLabel(input.getLabel());
                assessmentEntity.setResults(new HashMap<>());
                assessmentEntity.setCreatedBy(userId);
                assessmentEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setLastModifiedBy(userId);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

                AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                Assessment assessment = this.assessmentConverter.convert(descriptionEntity, savedAssessmentEntity);
                return new CreateAssessmentSuccessPayload(assessment);
            }
            return new ErrorPayload("The description does not exist");
        }
        return new ErrorPayload("The project does not exist");
    }
}
