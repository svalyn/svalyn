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
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.repositories.IAssessmentRepository;

@Service
@Transactional(readOnly = true)
public class AssessmentSearchService {

    private final AssessmentConverter assessmentConverter;

    private final IAssessmentRepository assessmentRepository;

    public AssessmentSearchService(AssessmentConverter assessmentConverter,
            IAssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public Optional<Assessment> findById(UUID projectId, UUID assessmentId) {
        // @formatter:off
        return this.assessmentRepository.findByProjectIdAndAssessmentId(projectId, assessmentId)
                .map(this.assessmentConverter::convert);
        // @formatter:on
    }

    public long countByProjectId(UUID projectId) {
        return this.assessmentRepository.countByProjectId(projectId);
    }

    public List<Assessment> findAllByProjectId(UUID projectId, Pageable pageable) {
        // @formatter:off
        return this.assessmentRepository.findAllByProjectId(projectId, pageable).stream()
                .map(this.assessmentConverter::convert)
                .collect(Collectors.toList());
        // @formatter:on
    }
}
