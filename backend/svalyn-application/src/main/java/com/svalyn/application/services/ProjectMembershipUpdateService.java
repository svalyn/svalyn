/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.AddMemberToProjectInput;
import com.svalyn.application.dto.input.RemoveMemberFromProjectInput;
import com.svalyn.application.dto.output.AddMemberToProjectSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.dto.output.RemoveMemberFromProjectSuccessPayload;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
public class ProjectMembershipUpdateService {

    private final IAccountRepository accountRepository;

    private final IProjectRepository projectRepository;

    private final ProjectConverter projectConverter;

    public ProjectMembershipUpdateService(IAccountRepository accountRepository, IProjectRepository projectRepository,
            ProjectConverter projectConverter) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.projectConverter = Objects.requireNonNull(projectConverter);
    }

    public IPayload addMember(AddMemberToProjectInput input) {
        var optionalMemberToAdd = this.accountRepository.findByUsername(input.getUsername());
        var optionalProject = this.projectRepository.findById(input.getProjectId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProject.isPresent()) {
            ProjectEntity projectEntity = optionalProject.get();

            var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>());

            // @formatter:off
            var isAlreadyMember = members.stream()
                    .map(AccountEntity::getUsername)
                    .anyMatch(input.getUsername()::equals);
            // @formatter:on
            if (!isAlreadyMember) {
                if (optionalMemberToAdd.isPresent()) {
                    AccountEntity memberToAdd = optionalMemberToAdd.get();
                    members.add(memberToAdd);
                    projectEntity.setMembers(members);

                    ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);
                    Project project = this.projectConverter.convert(savedProjectEntity);
                    payload = new AddMemberToProjectSuccessPayload(project);
                } else {
                    payload = new ErrorPayload("The user does not exist");
                }
            } else {
                payload = new ErrorPayload("The user is already a member");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }
        return payload;
    }

    public IPayload removeMember(RemoveMemberFromProjectInput input) {
        var optionalMemberToRemove = this.accountRepository.findByUsername(input.getUsername());
        var optionalProject = this.projectRepository.findById(input.getProjectId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProject.isPresent()) {
            if (optionalMemberToRemove.isPresent()) {
                ProjectEntity projectEntity = optionalProject.get();
                AccountEntity memberToRemove = optionalMemberToRemove.get();

                var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>());

                // @formatter:off
                var newMembers = members.stream()
                        .filter(member -> !member.getId().equals(memberToRemove.getId()))
                        .collect(Collectors.toList());
                // @formatter:on

                projectEntity.setMembers(newMembers);
                ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);
                Project project = this.projectConverter.convert(savedProjectEntity);
                payload = new RemoveMemberFromProjectSuccessPayload(project);
            } else {
                payload = new ErrorPayload("The user does not exist");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }

        return payload;
    }

}
