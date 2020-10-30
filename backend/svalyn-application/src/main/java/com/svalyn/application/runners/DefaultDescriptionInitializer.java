/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.runners;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.DescriptionInput;
import com.svalyn.application.repositories.IDescriptionRepository;
import com.svalyn.application.services.DescriptionConverter;

@Component
public class DefaultDescriptionInitializer implements CommandLineRunner {

    private final ObjectMapper objectMapper;

    private final DescriptionConverter descriptionConverter;

    private final IDescriptionRepository descriptionRepository;

    private final Logger logger = LoggerFactory.getLogger(DefaultDescriptionInitializer.class);

    public DefaultDescriptionInitializer(ObjectMapper objectMapper, DescriptionConverter descriptionConverter,
            IDescriptionRepository descriptionRepository) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.descriptionConverter = Objects.requireNonNull(descriptionConverter);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
    }

    @Override
    public void run(String... args) throws Exception {
        // @formatter:off
        List.of("/description/park.json", "/description/software.json").stream()
            .map(this::getDescription)
            .flatMap(Optional::stream)
            .map(this.descriptionConverter::convertToEntity)
            .filter(descriptionEntity -> !this.descriptionRepository.existsByLabel(descriptionEntity.getLabel()))
            .forEach(this.descriptionRepository::save);
        // @formatter:on
    }

    private Optional<DescriptionInput> getDescription(String path) {
        Optional<DescriptionInput> optionalDescriptionInput = Optional.empty();
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            DescriptionInput descriptionInput = this.objectMapper.readValue(inputStream, DescriptionInput.class);
            optionalDescriptionInput = Optional.of(descriptionInput);
        } catch (IOException exception) {
            this.logger.warn(exception.getMessage(), exception);
        }
        return optionalDescriptionInput;
    }

}
