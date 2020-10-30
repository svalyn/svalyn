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
import com.svalyn.application.repositories.IAssessmentRepository;

@Service
public class AssessmentDeletionService {

    private final IAssessmentRepository assessmentRepository;

    public AssessmentDeletionService(IAssessmentRepository assessmentRepository) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload deleteAssessments(DeleteAssessmentsInput input) {
        this.assessmentRepository.deleteWithIds(input.getAssessmentIds());
        return new DeleteAssessmentsSuccessPayload();
    }
}
