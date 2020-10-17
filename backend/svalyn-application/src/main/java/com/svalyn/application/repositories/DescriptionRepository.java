/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.entities.DescriptionEntity;

@Service
public class DescriptionRepository {

    private final ObjectMapper objectMapper;

    private final List<DescriptionEntity> descriptionEntities = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(DescriptionRepository.class);

    public DescriptionRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.addDescription("/description/park.json");
        this.addDescription("/description/software.json");
    }

    public void addDescription(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            DescriptionEntity descriptionEntity = this.objectMapper.readValue(inputStream, DescriptionEntity.class);
            this.descriptionEntities.add(descriptionEntity);
        } catch (IOException exception) {
            this.logger.warn(exception.getMessage(), exception);
        }
    }

    public Optional<DescriptionEntity> findDescriptionById(UUID descriptionId) {
        // @formatter:off
        return this.descriptionEntities.stream()
                .filter(description -> description.getId().equals(descriptionId))
                .findFirst();
        // @formatter:on
    }

    public List<DescriptionEntity> findAll() {
        return this.descriptionEntities;
    }
}
