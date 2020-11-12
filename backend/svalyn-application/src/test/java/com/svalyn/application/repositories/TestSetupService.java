/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;

public class TestSetupService {
    private final IAccountRepository accountRepository;

    private final IDescriptionRepository descriptionRepository;

    private final IProjectRepository projectRepository;

    private final IAssessmentRepository assessmentRepository;

    public TestSetupService(IAccountRepository accountRepository, IDescriptionRepository descriptionRepository,
            IProjectRepository projectRepository, IAssessmentRepository assessmentRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public Map<String, UUID> setup() {
        assertThat(this.accountRepository.count()).isZero();

        AccountEntity robert = this.createAccount("Robert");
        AccountEntity bernard = this.createAccount("Bernard");
        AccountEntity dolores = this.createAccount("Dolores");
        AccountEntity maeve = this.createAccount("Maeve");
        AccountEntity musashi = this.createAccount("Musashi");

        assertThat(this.projectRepository.count()).isZero();

        ProjectEntity westworld = this.createProject("Westworld", robert, robert, List.of(dolores, maeve));
        ProjectEntity eastworld = this.createProject("Eastworld", robert, robert, List.of(maeve, musashi));
        ProjectEntity northworld = this.createProject("Northworld", bernard, bernard, List.of(dolores));
        ProjectEntity southworld = this.createProject("Southworld", bernard, bernard, List.of(robert, dolores));

        DescriptionEntity descriptionEntity = this.createDescription();
        AssessmentEntity westworldOpeningAssessment = this.createAssessment(descriptionEntity, westworld, robert,
                robert);
        AssessmentEntity westworldMidSeasonAssessment = this.createAssessment(descriptionEntity, westworld, dolores,
                dolores);
        AssessmentEntity westworldEndSeasonAssessment = this.createAssessment(descriptionEntity, westworld, robert,
                robert);

        Map<String, UUID> data = new HashMap<>();
        data.put("Robert", robert.getId());
        data.put("Bernard", bernard.getId());
        data.put("Dolores", dolores.getId());
        data.put("Maeve", maeve.getId());
        data.put("Musashi", musashi.getId());
        data.put("Westworld", westworld.getId());
        data.put("Eastworld", eastworld.getId());
        data.put("Northworld", northworld.getId());
        data.put("Southworld", southworld.getId());
        data.put("WestworldOpeningAssessment", westworldOpeningAssessment.getId());
        data.put("WestworldMidSeasonAssessment", westworldMidSeasonAssessment.getId());
        data.put("WestworldEndSeasonAssessment", westworldEndSeasonAssessment.getId());
        return data;
    }

    private AccountEntity createAccount(String username) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUsername(username);
        accountEntity.setPassword("password");
        return this.accountRepository.save(accountEntity);
    }

    private DescriptionEntity createDescription() {
        DescriptionEntity descriptionEntity = new DescriptionEntity();
        descriptionEntity.setLabel("label");

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setLabel("label");
        categoryEntity.setDetails("details");

        RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setLabel("label");
        requirementEntity.setDetails("details");

        TestEntity testEntity = new TestEntity();
        testEntity.setLabel("label");
        testEntity.setDetails("details");
        testEntity.setSteps(List.of("one", "two", "three"));
        testEntity.setDescription(descriptionEntity);
        testEntity.setCategory(categoryEntity);
        testEntity.setRequirement(requirementEntity);

        requirementEntity.setDescription(descriptionEntity);
        requirementEntity.setCategory(categoryEntity);
        requirementEntity.setTests(List.of(testEntity));

        categoryEntity.setDescription(descriptionEntity);
        categoryEntity.setRequirements(List.of(requirementEntity));

        descriptionEntity.setCategories(List.of(categoryEntity));

        return this.descriptionRepository.save(descriptionEntity);
    }

    private ProjectEntity createProject(String label, AccountEntity ownedBy, AccountEntity createdBy,
            List<AccountEntity> members) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setLabel(label);
        projectEntity.setOwnedBy(ownedBy);
        projectEntity.setCreatedBy(createdBy);
        projectEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
        projectEntity.setMembers(members);
        return this.projectRepository.save(projectEntity);
    }

    private AssessmentEntity createAssessment(DescriptionEntity descriptionEntity, ProjectEntity projectEntity,
            AccountEntity createdBy, AccountEntity lastModifiedBy) {
        AssessmentEntity assessmentEntity = new AssessmentEntity();
        assessmentEntity.setDescription(descriptionEntity);
        assessmentEntity.setProject(projectEntity);
        assessmentEntity.setLabel("label");
        assessmentEntity.setCreatedBy(createdBy);
        assessmentEntity.setCreatedOn(LocalDateTime.now());
        assessmentEntity.setLastModifiedBy(lastModifiedBy);
        assessmentEntity.setLastModifiedOn(LocalDateTime.now());
        assessmentEntity.setResults(new ArrayList<>());
        assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);
        return this.assessmentRepository.save(assessmentEntity);
    }
}
