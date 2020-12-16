/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.DescriptionInput;

@Component
public class DefaultDescriptionProvider {

    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(DefaultDescriptionProvider.class);

    public DefaultDescriptionProvider(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public List<DescriptionInput> getDescriptions() {
        // @formatter:off
        return List.of("/description/park.json", "/description/software.json").stream()
            .map(this::getDescription)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
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
