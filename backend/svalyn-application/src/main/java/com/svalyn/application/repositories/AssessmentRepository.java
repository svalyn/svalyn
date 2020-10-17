/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AssessmentEntity;

@Service
public class AssessmentRepository {

    private final List<AssessmentEntity> assessmentEntities = new ArrayList<>();

    public List<AssessmentEntity> findAllByProjectId(UUID projectId, Pageable pageable) {
        int start = Long.valueOf(pageable.getOffset()).intValue();
        int end = start + pageable.getPageSize();

        if (start > this.assessmentEntities.size()) {
            start = this.assessmentEntities.size();
        }
        if (end > this.assessmentEntities.size()) {
            end = this.assessmentEntities.size();
        }

        // @formatter:off
        return this.assessmentEntities.subList(start, end).stream()
                .filter(assessmentEntity -> assessmentEntity.getProjectId().equals(projectId))
                .collect(Collectors.toList());
        // @formatter:on
    }

    public int countByProjectId(UUID projectId) {
        // formatter:off
        return this.assessmentEntities.stream().filter(assessment -> assessment.getProjectId().equals(projectId))
                .collect(Collectors.toList()).size();
        // formatter:on
    }

    public Optional<AssessmentEntity> findById(UUID assessmentId) {
        // @formatter:off
        return this.assessmentEntities.stream()
                .filter(assessment -> assessment.getId().equals(assessmentId))
                .findFirst();
        // @formatter:on
    }

    public boolean existById(UUID assessmentId) {
        return this.assessmentEntities.stream().anyMatch(assessment -> assessment.getId().equals(assessmentId));
    }

    public AssessmentEntity save(AssessmentEntity assessmentEntity) {
        var optionalAssessmentEntity = this.assessmentEntities.stream()
                .filter(entity -> entity.getId().equals(assessmentEntity.getId())).findFirst();

        if (optionalAssessmentEntity.isPresent()) {
            var entity = optionalAssessmentEntity.get();
            int index = this.assessmentEntities.indexOf(entity);
            this.assessmentEntities.remove(index);

            this.assessmentEntities.add(index, assessmentEntity);
        } else {
            this.assessmentEntities.add(0, assessmentEntity);
        }
        return assessmentEntity;
    }

    public void deleteAssessments(List<UUID> assessmentIds) {
        // @formatter:off
        var assessmentsToRemove = this.assessmentEntities.stream()
            .filter(assessment -> assessmentIds.contains(assessment.getId()))
            .collect(Collectors.toList());

        assessmentsToRemove.forEach(this.assessmentEntities::remove);
        // @formatter:on
    }

    public void deleteAllByProjectIds(List<UUID> projectIds) {
        // @formatter:off
        var assessments = this.assessmentEntities.stream()
                .filter(assessment -> projectIds.contains(assessment.getProjectId())).collect(Collectors.toList());
        assessments.stream().forEach(this.assessmentEntities::remove);
        // @formatter:on
    }

}
