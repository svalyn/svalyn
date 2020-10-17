/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Description;
import com.svalyn.application.repositories.DescriptionRepository;

@Service
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;

    public DescriptionService(DescriptionRepository descriptionRepository) {
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
    }

    public List<Description> getDescriptions() {
        // @formatter:off
        return this.descriptionRepository.findAll().stream()
                .map(descriptionEntity -> new Description(descriptionEntity.getId(), descriptionEntity.getLabel()))
                .collect(Collectors.toList());
        // @formatter:on

    }
}
