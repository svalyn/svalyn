/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.UpdateAssessmentStatusSuccessPayload;
import com.svalyn.application.dto.output.UpdateTestSuccessPayload;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.entities.TestResultEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IAssessmentRepository;

@Service
@Transactional
public class AssessmentUpdateService {

    private final AssessmentConverter assessmentConverter;

    private final IAccountRepository accountRepository;

    private final IAssessmentRepository assessmentRepository;

    public AssessmentUpdateService(AssessmentConverter assessmentConverter, IAccountRepository accountRepository,
            IAssessmentRepository assessmentRepository) {
        this.assessmentConverter = Objects.requireNonNull(assessmentConverter);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public IPayload updateAssessmentStatus(UUID userId, UpdateAssessmentStatusInput input) {
        IPayload payload = new ErrorPayload("An unexpected error has occurred");

        var opttionalAccountEntity = this.accountRepository.findById(userId);
        var optionalAssessmentEntity = this.assessmentRepository.findByUserIdAndAssessmentId(userId,
                input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent() && opttionalAccountEntity.isPresent()) {
            AccountEntity accountEntity = opttionalAccountEntity.get();
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();
            assessmentEntity.setStatus(this.assessmentConverter.convert(input.getStatus()));
            assessmentEntity.setLastModifiedBy(accountEntity);
            assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

            AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
            Assessment assessment = this.assessmentConverter.convert(savedAssessmentEntity);
            payload = new UpdateAssessmentStatusSuccessPayload(assessment);
        } else {
            payload = new ErrorPayload("The assessment does not exist");
        }
        return payload;
    }

    public IPayload updateTest(UUID userId, UpdateTestInput input) {
        IPayload payload = new ErrorPayload("An unexpected error has occurred");

        var opttionalAccountEntity = this.accountRepository.findById(userId);
        var optionalAssessmentEntity = this.assessmentRepository.findByUserIdAndAssessmentId(userId,
                input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent() && opttionalAccountEntity.isPresent()) {
            AccountEntity accountEntity = opttionalAccountEntity.get();
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();

            if (assessmentEntity.getStatus() == AssessmentStatusEntity.OPEN) {
                var statusEntity = this.assessmentConverter.convert(input.getStatus());
                assessmentEntity.setLastModifiedBy(accountEntity);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

                // @formatter:off
                var optionalTestResultEntity = assessmentEntity.getResults().stream()
                        .filter(testResultEntity -> testResultEntity.getTest().getId().equals(input.getTestId()))
                        .findFirst();
                var optionalTestEntity = assessmentEntity.getDescription().getCategories().stream()
                        .map(CategoryEntity::getRequirements)
                        .flatMap(Collection::stream)
                        .map(RequirementEntity::getTests)
                        .flatMap(Collection::stream)
                        .filter(testEntity -> testEntity.getId().equals(input.getTestId()))
                        .findFirst();
                // @formatter:on

                if (optionalTestResultEntity.isPresent()) {
                    TestResultEntity testResultEntity = optionalTestResultEntity.get();
                    testResultEntity.setStatus(statusEntity);

                    AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                    Assessment assessment = this.assessmentConverter.convert(savedAssessmentEntity);
                    payload = new UpdateTestSuccessPayload(assessment);
                } else if (optionalTestEntity.isPresent()) {
                    TestEntity testEntity = optionalTestEntity.get();

                    TestResultEntity testResultEntity = new TestResultEntity();
                    testResultEntity.setStatus(statusEntity);
                    testResultEntity.setAssessment(assessmentEntity);
                    testResultEntity.setTest(testEntity);
                    testResultEntity.setRequirement(testEntity.getRequirement());
                    testResultEntity.setCategory(testEntity.getCategory());
                    testResultEntity.setDescription(testEntity.getDescription());
                    assessmentEntity.getResults().add(testResultEntity);

                    AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                    Assessment assessment = this.assessmentConverter.convert(savedAssessmentEntity);
                    payload = new UpdateTestSuccessPayload(assessment);
                }
            }
        } else {
            payload = new ErrorPayload("The assessment does not exist");
        }
        return payload;
    }
}
