/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class GraphQLDateCoercing implements Coercing<LocalDateTime, String> {

    private Optional<LocalDateTime> convert(Object input) {
        Optional<LocalDateTime> optionalLocalDateTime = Optional.empty();
        if (input instanceof String) {
            String value = (String) input;
            LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_INSTANT);
            optionalLocalDateTime = Optional.of(localDateTime);
        } else if (input instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) input;
            optionalLocalDateTime = Optional.of(localDateTime);
        }
        return optionalLocalDateTime;
    }

    @Override
    public String serialize(Object result) throws CoercingSerializeException {
        if (result instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) result;
            return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.of(localDateTime, ZoneOffset.UTC));
        }
        return null;
    }

    @Override
    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
        return this.convert(input).orElseThrow(
                () -> new CoercingParseValueException("The value " + input + " is not a valid LocalDateTime"));
    }

    @Override
    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
        // @formatter:off
        return Optional.of(input).filter(StringValue.class::isInstance)
                .map(StringValue.class::cast)
                .map(StringValue::getValue)
                .flatMap(this::convert)
                .orElse(null);
        // @formatter:on
    }

}
