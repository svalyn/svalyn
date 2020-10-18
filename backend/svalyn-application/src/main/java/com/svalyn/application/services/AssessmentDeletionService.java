/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.DeleteAssessmentsInput;
import com.svalyn.application.dto.output.DeleteAssessmentsSuccessPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.repositories.AssessmentRepository;

@Service
public class AssessmentDeletionService {
    private final AssessmentRepository assessmentRepository;

    public AssessmentDeletionService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload deleteAssessments(DeleteAssessmentsInput input) {
        this.assessmentRepository.deleteAssessments(input.getAssessmentIds());
        return new DeleteAssessmentsSuccessPayload();
    }
}
