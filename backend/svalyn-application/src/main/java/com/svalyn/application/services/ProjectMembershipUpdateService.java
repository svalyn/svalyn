/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.input.AddMemberToProjectInput;
import com.svalyn.application.dto.input.ChangeProjectOwnerInput;
import com.svalyn.application.dto.input.LeaveProjectInput;
import com.svalyn.application.dto.input.RemoveMemberFromProjectInput;
import com.svalyn.application.dto.output.AddMemberToProjectSuccessPayload;
import com.svalyn.application.dto.output.ChangeProjectOwnerSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.LeaveProjectSuccessPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.dto.output.RemoveMemberFromProjectSuccessPayload;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.repositories.IAccountRepository;
import com.svalyn.application.repositories.IProjectRepository;

@Service
@Transactional
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

    public IPayload addMember(UUID userId, AddMemberToProjectInput input) {
        var optionalMemberToAdd = this.accountRepository.findByUsername(input.getUsername());
        var optionalProjectEntity = this.projectRepository.findByUserIdAndProjectId(userId, input.getProjectId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProjectEntity.isPresent()) {
            ProjectEntity projectEntity = optionalProjectEntity.get();

            var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>());

            // @formatter:off
            var isMemberToAddAMember = members.stream()
                    .map(AccountEntity::getUsername)
                    .anyMatch(input.getUsername()::equals);
            // @formatter:on

            var isMemberToAddTheOwner = projectEntity.getOwnedBy().getUsername().equals(input.getUsername());

            var isCurrentUserTheOwner = projectEntity.getOwnedBy().getId().equals(userId);

            if (!isCurrentUserTheOwner) {
                payload = new ErrorPayload("You are not authorized to perform this action");
            } else if (isMemberToAddAMember || isMemberToAddTheOwner) {
                payload = new ErrorPayload("The user is already a member or the owner");
            } else {
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
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }
        return payload;
    }

    public IPayload removeMember(UUID userId, RemoveMemberFromProjectInput input) {
        var optionalMemberToRemove = this.accountRepository.findByUsername(input.getUsername());
        var optionalProjectEntity = this.projectRepository.findByUserIdAndProjectId(userId, input.getProjectId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProjectEntity.isPresent()) {
            if (optionalMemberToRemove.isPresent()) {
                ProjectEntity projectEntity = optionalProjectEntity.get();
                AccountEntity memberToRemove = optionalMemberToRemove.get();

                var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>());

                // @formatter:off
                var newMembers = members.stream()
                        .filter(member -> !member.getId().equals(memberToRemove.getId()))
                        .collect(Collectors.toList());
                // @formatter:on

                var isCurrentUserTheOwner = projectEntity.getOwnedBy().getId().equals(userId);

                if (!isCurrentUserTheOwner) {
                    payload = new ErrorPayload("You are not authorized to perform this action");
                } else {
                    projectEntity.setMembers(newMembers);
                    ProjectEntity savedProjectEntity = this.projectRepository.save(projectEntity);
                    Project project = this.projectConverter.convert(savedProjectEntity);
                    payload = new RemoveMemberFromProjectSuccessPayload(project);
                }
            } else {
                payload = new ErrorPayload("The user does not exist");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }

        return payload;
    }

    public IPayload leaveProject(UserDetails userDetails, LeaveProjectInput input) {
        var optionalMemberToRemove = this.accountRepository.findById(userDetails.getId());
        var optionalProjectEntity = this.projectRepository.findByUserIdAndProjectId(userDetails.getId(),
                input.getProjectId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProjectEntity.isPresent()) {
            if (optionalMemberToRemove.isPresent()) {
                ProjectEntity projectEntity = optionalProjectEntity.get();
                AccountEntity memberToRemove = optionalMemberToRemove.get();
                var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>());

                // @formatter:off
                var newMembers = members.stream()
                        .filter(member -> !member.getId().equals(memberToRemove.getId()))
                        .collect(Collectors.toList());
                // @formatter:on

                var isCurrentUserTheOwner = projectEntity.getOwnedBy().getId().equals(userDetails.getId());

                if (isCurrentUserTheOwner) {
                    payload = new ErrorPayload("Delete the project instead");
                } else {
                    projectEntity.setMembers(newMembers);
                    this.projectRepository.save(projectEntity);

                    payload = new LeaveProjectSuccessPayload(userDetails);
                }
            } else {
                payload = new ErrorPayload("The user does not exist");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }

        return payload;
    }

    public IPayload changeOwner(UUID userId, ChangeProjectOwnerInput input) {
        var optionalAccount = this.accountRepository.findById(userId);
        var optionalProjectEntity = this.projectRepository.findByUserIdAndProjectId(userId, input.getProjectId());

        var optionalNewOwner = this.accountRepository.findById(input.getNewOwnerId());

        IPayload payload = new ErrorPayload("An unexpected error has occurred");
        if (optionalProjectEntity.isPresent()) {
            if (optionalAccount.isPresent() && optionalNewOwner.isPresent()) {
                ProjectEntity projectEntity = optionalProjectEntity.get();
                AccountEntity account = optionalAccount.get();
                AccountEntity newOwner = optionalNewOwner.get();

                boolean exists = this.projectRepository.existsByUserIdAndLabel(input.getNewOwnerId(),
                        projectEntity.getLabel());

                boolean isOwner = projectEntity.getOwnedBy().getId().equals(account.getId());
                // @formatter:off
                boolean isNewOwnerAnExistingMember = projectEntity.getMembers().stream()
                        .map(AccountEntity::getId)
                        .anyMatch(newOwner.getId()::equals);
                // @formatter:on

                if (!isOwner) {
                    payload = new ErrorPayload("You are not authorized to perform this action");
                } else if (!isNewOwnerAnExistingMember) {
                    payload = new ErrorPayload("The new owner must be an existing member");
                } else if (exists) {
                    payload = new ErrorPayload("The new owner already has a project with this name");
                } else {
                    List<AccountEntity> newMembers = new ArrayList<>();
                    // @formatter:off
                    Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>()).stream()
                            .filter(member -> !member.getId().equals(newOwner.getId()))
                            .forEach(newMembers::add);
                    // @formatter:on
                    newMembers.add(account);

                    projectEntity.setOwnedBy(newOwner);
                    projectEntity.setMembers(newMembers);
                    this.projectRepository.save(projectEntity);

                    Project project = this.projectConverter.convert(projectEntity);
                    payload = new ChangeProjectOwnerSuccessPayload(project);
                }
            } else {
                payload = new ErrorPayload("The user does not exist");
            }
        } else {
            payload = new ErrorPayload("The project does not exist");
        }

        return payload;
    }

}
