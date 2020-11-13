/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import static graphql.schema.FieldCoordinates.coordinates;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.svalyn.application.graphql.GraphQLDateCoercing;
import com.svalyn.application.graphql.MutationAddMemberToProjectDataFetcher;
import com.svalyn.application.graphql.MutationCreateAssessmentDataFetcher;
import com.svalyn.application.graphql.MutationCreateProjectDataFetcher;
import com.svalyn.application.graphql.MutationDeleteAssessmentsDataFetcher;
import com.svalyn.application.graphql.MutationDeleteProjectsDataFetcher;
import com.svalyn.application.graphql.MutationLeaveProjectDataFetcher;
import com.svalyn.application.graphql.MutationRemoveMemberFromProjectDataFetcher;
import com.svalyn.application.graphql.MutationUpdateAssessmentStatusDataFetcher;
import com.svalyn.application.graphql.MutationUpdateTestDataFetcher;
import com.svalyn.application.graphql.ProjectAssessmentDataFetcher;
import com.svalyn.application.graphql.ProjectAssessmentsDataFetcher;
import com.svalyn.application.graphql.QueryDescriptionsDataFetcher;
import com.svalyn.application.graphql.QueryPrincipalDataFetcher;
import com.svalyn.application.graphql.QueryProjectDataFetcher;
import com.svalyn.application.graphql.QueryProjectsDataFetcher;

import graphql.GraphQL;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLScalarType;
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

        GraphQLSchema graphQLSchema = null;
        try (var inputStream = schemaResource.getInputStream();) {
            var typeDefinitionRegistry = new SchemaParser().parse(inputStream);

            TypeResolver defaultTypeResolver = environment -> {
                var className = environment.getObject().getClass().getSimpleName();
                return environment.getSchema().getObjectType(className);
            };

            // @formatter:off
            var runtimeWiring = RuntimeWiring.newRuntimeWiring().codeRegistry(graphQLCodeRegistry)
                    .scalar(this.getGraphQLDate())
                    .type("CreateProjectPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("CreateAssessmentPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("UpdateAssessmentStatusPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("UpdateTestPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("DeleteProjectsPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("DeleteAssessmentsPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("AddMemberToProjectPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("RemoveMemberFromProjectPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .type("LeaveProjectPayload", typeWiring -> typeWiring.typeResolver(defaultTypeResolver))
                    .build();
            // @formatter:on

            graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        } catch (IOException exception) {
            this.logger.warn(exception.getMessage(), exception);
        }
        return graphQLSchema;
    }

    @Bean
    public GraphQLCodeRegistry graphQLCodeRegistry(QueryPrincipalDataFetcher queryPrincipalDataFetcher,
            QueryDescriptionsDataFetcher queryDescriptionsDataFetcher,
            QueryProjectsDataFetcher queryProjectsDataFetcher, QueryProjectDataFetcher queryProjectDataFetcher,
            ProjectAssessmentsDataFetcher projectAssessmentsDataFetcher,
            ProjectAssessmentDataFetcher projectAssessmentDataFetcher,
            MutationAddMemberToProjectDataFetcher mutationAddMemberToProjectDataFetcher,
            MutationRemoveMemberFromProjectDataFetcher mutationRemoveMemberFromProjectDataFetcher,
            MutationCreateProjectDataFetcher mutationCreateProjectDataFetcher,
            MutationCreateAssessmentDataFetcher mutationCreateAssessmentDataFetcher,
            MutationUpdateAssessmentStatusDataFetcher mutationUpdateAssessmentStatusDataFetcher,
            MutationUpdateTestDataFetcher mutationUpdateTestDataFetcher,
            MutationDeleteProjectsDataFetcher mutationDeleteProjectsDataFetcher,
            MutationDeleteAssessmentsDataFetcher mutationDeleteAssessmentsDataFetcher,
            MutationLeaveProjectDataFetcher mutationLeaveProjectDataFetcher) {

        // @formatter:off
        return GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(coordinates("Query", "principal"), queryPrincipalDataFetcher)
                .dataFetcher(coordinates("Query", "descriptions"), queryDescriptionsDataFetcher)
                .dataFetcher(coordinates("Query", "projects"), queryProjectsDataFetcher)
                .dataFetcher(coordinates("Query", "project"), queryProjectDataFetcher)
                .dataFetcher(coordinates("Mutation", "addMemberToProject"), mutationAddMemberToProjectDataFetcher)
                .dataFetcher(coordinates("Mutation", "removeMemberFromProject"), mutationRemoveMemberFromProjectDataFetcher)
                .dataFetcher(coordinates("Mutation", "createProject"), mutationCreateProjectDataFetcher)
                .dataFetcher(coordinates("Mutation", "createAssessment"), mutationCreateAssessmentDataFetcher)
                .dataFetcher(coordinates("Mutation", "updateAssessmentStatus"), mutationUpdateAssessmentStatusDataFetcher)
                .dataFetcher(coordinates("Mutation", "updateTest"), mutationUpdateTestDataFetcher)
                .dataFetcher(coordinates("Mutation", "deleteProjects"), mutationDeleteProjectsDataFetcher)
                .dataFetcher(coordinates("Mutation", "deleteAssessments"), mutationDeleteAssessmentsDataFetcher)
                .dataFetcher(coordinates("Mutation", "leaveProject"), mutationLeaveProjectDataFetcher)
                .dataFetcher(coordinates("Project", "assessments"), projectAssessmentsDataFetcher)
                .dataFetcher(coordinates("Project", "assessment"), projectAssessmentDataFetcher)
                .build();
        // @formatter:on
    }

    private GraphQLScalarType getGraphQLDate() {
        // @formatter:off
        return GraphQLScalarType.newScalar()
                .name("Date") //$NON-NLS-1$
                .coercing(new GraphQLDateCoercing())
                .build();
        // @formatter:on
    }
}
