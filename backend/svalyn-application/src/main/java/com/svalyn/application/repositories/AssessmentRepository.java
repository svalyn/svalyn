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

import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AssessmentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentRepository {

    private final List<AssessmentEntity> assessmentEntities = new ArrayList<>();

    public Flux<AssessmentEntity> findAll() {
        return Flux.fromIterable(this.assessmentEntities);
    }

    public Mono<AssessmentEntity> findById(UUID assessmentId) {
        // @formatter:off
        var optionalAssessment = this.assessmentEntities.stream()
                .filter(assessment -> assessment.getId().equals(assessmentId))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalAssessment);
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

}
