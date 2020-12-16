/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.dto.output.Description;
import com.svalyn.application.repositories.IDescriptionRepository;

@Service
@Transactional(readOnly = true)
public class DescriptionSearchService {

    private final IDescriptionRepository descriptionRepository;

    public DescriptionSearchService(IDescriptionRepository descriptionRepository) {
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
    }

    public List<Description> getDescriptions(UUID projectId) {
        // @formatter:off
        return this.descriptionRepository.findAllByProjectId(projectId).stream()
                .map(descriptionEntity -> new Description(descriptionEntity.getId(), descriptionEntity.getLabel()))
                .collect(Collectors.toList());
        // @formatter:on

    }
}
