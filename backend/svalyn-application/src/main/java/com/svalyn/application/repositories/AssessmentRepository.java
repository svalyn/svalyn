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

import com.svalyn.application.dto.output.Assessment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentRepository {

    private final List<Assessment> assessments = new ArrayList<>();

    public Flux<Assessment> findAll() {
        return Flux.fromIterable(this.assessments);
    }

    public Mono<Assessment> findById(UUID assessmentId) {
        // @formatter:off
        var optionalAssessment = this.assessments.stream()
                .filter(assessment -> assessment.getId().equals(assessmentId))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalAssessment);
    }

    public Mono<Assessment> save(Assessment assessment) {
        this.assessments.add(assessment);
        return Mono.just(assessment);
    }

}
