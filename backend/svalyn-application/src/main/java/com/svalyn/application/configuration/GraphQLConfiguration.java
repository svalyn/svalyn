/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import static graphql.schema.FieldCoordinates.coordinates;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.svalyn.application.graphql.MutationCreateAssessmentDataFetcher;
import com.svalyn.application.graphql.MutationUpdateTestDataFetcher;
import com.svalyn.application.graphql.ProjectAssessmentDataFetcher;
import com.svalyn.application.graphql.ProjectAssessmentsDataFetcher;
import com.svalyn.application.graphql.QueryDescriptionsDataFetcher;
import com.svalyn.application.graphql.QueryProjectDataFetcher;
import com.svalyn.application.graphql.QueryProjectsDataFetcher;

import graphql.GraphQL;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

@Configuration
public class GraphQLConfiguration {
    private static final String SCHEMA_PATH = "schema.graphqls"; //$NON-NLS-1$

    private final Logger logger = LoggerFactory.getLogger(GraphQLConfiguration.class);

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        // @formatter:off
		return GraphQL.newGraphQL(graphQLSchema)
		        .build();
		// @formatter:on
    }

    @Bean
    public GraphQLSchema graphQLSchema(GraphQLCodeRegistry graphQLCodeRegistry) {
        var schemaResource = new ClassPathResource(SCHEMA_PATH);
        Optional<InputStream> optionalInputStream = Optional.empty();
        try {
            optionalInputStream = Optional.of(schemaResource.getInputStream());
        } catch (IOException exception) {
            this.logger.warn(exception.getMessage(), exception);
        }

        // @formatter:off
        return optionalInputStream.map(inputStream -> {
            var typeDefinitionRegistry = new SchemaParser().parse(inputStream);

            TypeResolver defaultTypeResolver = environment -> {
                var className = environment.getObject().getClass().getSimpleName();
                return environment.getSchema().getObjectType(className);
            };

            var runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .codeRegistry(graphQLCodeRegistry)
                    .type("CreateAssessmentPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("UpdateTestPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .build();

            return new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        }).orElse(null);
        // @formatter:on
    }

    @Bean
    public GraphQLCodeRegistry graphQLCodeRegistry(QueryDescriptionsDataFetcher queryDescriptionsDataFetcher,
            QueryProjectsDataFetcher queryProjectsDataFetcher, QueryProjectDataFetcher queryProjectDataFetcher,
            ProjectAssessmentsDataFetcher projectAssessmentsDataFetcher,
            ProjectAssessmentDataFetcher projectAssessmentDataFetcher,
            MutationCreateAssessmentDataFetcher mutationCreateAssessmentDataFetcher,
            MutationUpdateTestDataFetcher mutationUpdateTestDataFetcher) {

        // @formatter:off
        return GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(coordinates("Query", "descriptions"), queryDescriptionsDataFetcher)
                .dataFetcher(coordinates("Query", "projects"), queryProjectsDataFetcher)
                .dataFetcher(coordinates("Query", "project"), queryProjectDataFetcher)
                .dataFetcher(coordinates("Mutation", "createAssessment"), mutationCreateAssessmentDataFetcher)
                .dataFetcher(coordinates("Mutation", "updateTest"), mutationUpdateTestDataFetcher)
                .dataFetcher(coordinates("Project", "assessments"), projectAssessmentsDataFetcher)
                .dataFetcher(coordinates("Project", "assessment"), projectAssessmentDataFetcher)
                .build();
        // @formatter:on
    }
}
