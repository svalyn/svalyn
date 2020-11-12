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

import com.svalyn.application.entities.ProjectEntity;

@Repository
public interface IProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    // @formatter:off
    @Query("""
    SELECT DISTINCT project FROM ProjectEntity project
    LEFT JOIN project.members projectMember
    WHERE project.id = :projectId AND (project.ownedBy.id = :userId OR projectMember.id = :userId)
    """)
    Optional<ProjectEntity> findByUserIdAndProjectId(@Param("userId") UUID userId, @Param("projectId") UUID projectId);

    @Query("""
    SELECT DISTINCT project FROM ProjectEntity project
    LEFT JOIN project.members projectMember
    WHERE project.ownedBy.id = :userId OR projectMember.id = :userId
    """)
    List<ProjectEntity> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
    SELECT COUNT(DISTINCT project) FROM ProjectEntity project
    LEFT JOIN project.members projectMember
    WHERE project.ownedBy.id = :userId OR projectMember.id = :userId
    """)
    long countByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT
      CASE
        WHEN COUNT(project) > 0 THEN TRUE
        ELSE FALSE
      END
    FROM ProjectEntity project
    WHERE project.label = :label AND project.ownedBy.id = :userId
    """)
    boolean existsByUserIdAndLabel(@Param("userId") UUID userId, @Param("label") String label);

    @Query("""
    SELECT
      CASE
        WHEN COUNT(project) > 0 THEN TRUE
        ELSE FALSE
      END
    FROM ProjectEntity project
    LEFT JOIN project.members projectMember
    WHERE project.id = :projectId AND (project.ownedBy.id = :userId OR projectMember.id = :userId)
    """)
    boolean isVisibleByUserIdAndProjectId(@Param("userId") UUID userId, @Param("projectId") UUID projectId);

    @Query("""
    DELETE FROM ProjectEntity projectToDelete
    WHERE projectToDelete.id IN (
      SELECT project.id FROM ProjectEntity project
      WHERE project.id IN :projectIds AND project.ownedBy.id = :userId
    )
    """)
    @Modifying
    @Transactional
    void deleteByUserIdAndProjectIds(@Param("userId") UUID userId, @Param("projectIds") List<UUID> projectIds);


    @Query(value = """
    SELECT bool_and(ownership)
    FROM (
      SELECT
      CASE
        WHEN project.ownedBy = :userId THEN TRUE
        ELSE FALSE
      END AS ownership
      FROM Project project
      WHERE project.id IN :projectIds
    ) AS ownershipResults
    """,
    nativeQuery = true)
    boolean ownsAllByIds(@Param("userId") UUID userId, @Param("projectIds") List<UUID> projectIds);
}
