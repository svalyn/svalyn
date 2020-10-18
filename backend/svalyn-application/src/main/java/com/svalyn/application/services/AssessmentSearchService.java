/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.DescriptionRepository;

@Service
public class AssessmentSearchService {

    private final AssessmentConverter assessmentConverter;

    private final DescriptionRepository descriptionRepository;

    private final AssessmentRepository assessmentRepository;

    public AssessmentSearchService(AssessmentConverter assessmentConverter, DescriptionRepository descriptionRepository,
            AssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public int countByProjectId(UUID projectId) {
        return this.assessmentRepository.countByProjectId(projectId);
    }

    public List<Assessment> findAllByProjectId(UUID projectId, Pageable pageable) {
        // @formatter:off
        return this.assessmentRepository.findAllByProjectId(projectId, pageable).stream()
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.assessmentConverter.convert(descriptionEntity, assessmentEntity));
                    return optionalAssessment.stream();
                }).collect(Collectors.toList());
        // @formatter:on
    }

    public Optional<Assessment> findById(UUID assessmentId) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    return optionalDescriptionEntity.map(descriptionEntity -> this.assessmentConverter.convert(descriptionEntity, assessmentEntity));
                });
        // @formatter:on
    }
}
