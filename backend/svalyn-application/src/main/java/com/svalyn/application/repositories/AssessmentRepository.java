/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AssessmentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentRepository {

    private final List<AssessmentEntity> assessmentEntities = new ArrayList<>();

    public Flux<AssessmentEntity> findAllByProjectId(UUID projectId, Pageable pageable) {
        int start = Long.valueOf(pageable.getOffset()).intValue();
        int end = start + pageable.getPageSize();

        if (start > this.assessmentEntities.size()) {
            start = this.assessmentEntities.size();
        }
        if (end > this.assessmentEntities.size()) {
            end = this.assessmentEntities.size();
        }

        return Flux.fromIterable(this.assessmentEntities.subList(start, end))
                .filter(assessmentEntity -> assessmentEntity.getProjectId().equals(projectId));
    }

    public Mono<Long> countByProjectId(UUID projectId) {
        long count = this.assessmentEntities.stream().filter(assessment -> assessment.getProjectId().equals(projectId))
                .count();
        return Mono.just(count);
    }

    public Mono<AssessmentEntity> findById(UUID assessmentId) {
        // @formatter:off
        var optionalAssessment = this.assessmentEntities.stream()
                .filter(assessment -> assessment.getId().equals(assessmentId))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalAssessment);
    }

    public Mono<Boolean> existById(UUID assessmentId) {
        boolean existById = this.assessmentEntities.stream()
                .anyMatch(assessment -> assessment.getId().equals(assessmentId));
        return Mono.just(existById);
    }

    public Mono<AssessmentEntity> save(AssessmentEntity assessmentEntity) {
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
        return Mono.just(assessmentEntity);
    }

    public Mono<Void> deleteAssessment(UUID assessmentId) {
        // @formatter:off
        this.assessmentEntities.stream().filter(assessment -> assessment.getId().equals(assessmentId))
            .findFirst()
            .ifPresent(this.assessmentEntities::remove);
        // @formatter:on
        return Mono.empty();
    }

    public Mono<Void> deleteAllByProjectId(UUID projectId) {
        var assessments = this.assessmentEntities.stream()
                .filter(assessment -> assessment.getProjectId().equals(projectId)).collect(Collectors.toList());
        assessments.stream().forEach(this.assessmentEntities::remove);
        return Mono.empty();
    }

}
