/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Description;
import com.svalyn.application.repositories.DescriptionRepository;

import reactor.core.publisher.Flux;

@Service
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;

    public DescriptionService(DescriptionRepository descriptionRepository) {
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
    }

    public Flux<Description> getDescriptions() {
        return this.descriptionRepository.findAll()
                .map(descriptionEntity -> new Description(descriptionEntity.getId(), descriptionEntity.getLabel()));

    }
}
