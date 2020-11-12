/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.entities.AssessmentEntity;

@Repository
public interface IAssessmentRepository extends JpaRepository<AssessmentEntity, UUID> {
    // @formatter:off
    @Query("""
    SELECT DISTINCT assessment FROM AssessmentEntity assessment
    INNER JOIN assessment.project project
    LEFT JOIN project.members projectMember
    WHERE assessment.id = :assessmentId AND (project.ownedBy.id = :userId OR projectMember.id = :userId)
    """)
    Optional<AssessmentEntity> findByUserIdAndAssessmentId(@Param("userId") UUID userId, @Param("assessmentId") UUID assessmentId);

    long countByProjectId(UUID projectId);

    List<AssessmentEntity> findAllByProjectId(UUID projectId, Pageable pageable);

    @Query("""
    SELECT assessment FROM AssessmentEntity assessment
    WHERE assessment.project.id = :projectId AND assessment.id = :assessmentId
    """)
    Optional<AssessmentEntity> findByProjectIdAndAssessmentId(@Param("projectId") UUID projectId, @Param("assessmentId") UUID assessmentId);

    @Query("""
    DELETE FROM AssessmentEntity assessmentToDelete
    WHERE assessmentToDelete.id IN (
      SELECT assessment.id FROM AssessmentEntity assessment
      INNER JOIN assessment.project project
      LEFT JOIN project.members projectMember
      WHERE assessment.id IN :assessmentIds AND (project.ownedBy.id = :userId OR projectMember.id = :userId)
    )
    """)
    @Modifying
    @Transactional
    void deleteWithIds(@Param("userId") UUID userId, @Param("assessmentIds") List<UUID> assessmentIds);

    @Query(value = """
    SELECT bool_and(isInProject)
    FROM (
      SELECT
      CASE
        WHEN assessment.projectId = :projectId THEN TRUE
        ELSE FALSE
      END AS isInProject
      FROM Assessment assessment
      WHERE assessment.id IN :assessmentIds
    ) AS isInProjectResults
    """,
    nativeQuery = true)
    boolean areAllInProject(@Param("projectId") UUID projectId, @Param("assessmentIds") List<UUID> assessmentIds);

}
