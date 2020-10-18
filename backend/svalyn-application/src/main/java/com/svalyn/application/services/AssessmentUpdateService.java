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

import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.UpdateAssessmentStatusSuccessPayload;
import com.svalyn.application.dto.output.UpdateTestSuccessPayload;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.DescriptionRepository;

@Service
public class AssessmentUpdateService {

    private final AssessmentConverter assessmentConverter;

    private final DescriptionRepository descriptionRepository;

    private final AssessmentRepository assessmentRepository;

    public AssessmentUpdateService(AssessmentConverter assessmentConverter, DescriptionRepository descriptionRepository,
            AssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload updateAssessmentStatus(UUID userId, UpdateAssessmentStatusInput input) {
        var optionalAssessmentEntity = this.assessmentRepository.findById(input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent()) {
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();
            assessmentEntity.setStatus(this.assessmentConverter.convert(input.getStatus()));
            assessmentEntity.setLastModifiedBy(userId);
            assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

            AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
            var optionalDescriptionEntity = this.descriptionRepository
                    .findDescriptionById(savedAssessmentEntity.getDescriptionId());
            var optionalAssessment = optionalDescriptionEntity.map(
                    descriptionEntity -> this.assessmentConverter.convert(descriptionEntity, savedAssessmentEntity));
            if (optionalAssessment.isPresent()) {
                var assessment = optionalAssessment.get();
                return new UpdateAssessmentStatusSuccessPayload(assessment);
            }
        }
        return new ErrorPayload("The assessment does not exist");
    }

    public IPayload updateTest(UUID userId, UpdateTestInput input) {
        var optionalAssessmentEntity = this.assessmentRepository.findById(input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent()) {
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();
            if (assessmentEntity.getStatus() == AssessmentStatusEntity.OPEN) {
                var statusEntity = this.assessmentConverter.convert(input.getStatus());
                assessmentEntity.getResults().put(input.getTestId(), statusEntity);
                assessmentEntity.setLastModifiedBy(userId);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

                AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                var optionalDescriptionEntity = this.descriptionRepository
                        .findDescriptionById(savedAssessmentEntity.getDescriptionId());
                var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.assessmentConverter
                        .convert(descriptionEntity, savedAssessmentEntity));
                if (optionalAssessment.isPresent()) {
                    var assessment = optionalAssessment.get();
                    return new UpdateTestSuccessPayload(assessment);
                }
            }
        }
        return new ErrorPayload("The assessment does not exist");
    }
}
