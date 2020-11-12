/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.input.DeleteAssessmentsInput;
import com.svalyn.application.dto.output.DeleteAssessmentsSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.repositories.IAssessmentRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
@Transactional
public class AssessmentDeletionService {

    private final IProjectRepository projectRepository;

    private final IAssessmentRepository assessmentRepository;

    public AssessmentDeletionService(IProjectRepository projectRepository, IAssessmentRepository assessmentRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload deleteAssessments(UUID userId, DeleteAssessmentsInput input) {
        IPayload payload = new DeleteAssessmentsSuccessPayload();
        if (!input.getAssessmentIds().isEmpty()) {
            if (!this.projectRepository.isVisibleByUserIdAndProjectId(userId, input.getProjectId())) {
                payload = new ErrorPayload("The project does not exist");
            } else if (!this.assessmentRepository.areAllInProject(input.getProjectId(), input.getAssessmentIds())) {
                payload = new ErrorPayload("Some assessments are not in the project");
            } else {
                this.assessmentRepository.deleteWithIds(userId, input.getAssessmentIds());
                payload = new DeleteAssessmentsSuccessPayload();
            }
        }
        return payload;
    }
}
