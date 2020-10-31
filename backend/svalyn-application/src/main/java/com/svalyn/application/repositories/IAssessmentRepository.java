/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.entities.AssessmentEntity;

@Repository
public interface IAssessmentRepository extends JpaRepository<AssessmentEntity, UUID> {
    long countByProjectId(UUID projectId);

    List<AssessmentEntity> findAllByProjectId(UUID projectId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM AssessmentEntity assessmentEntity where assessmentEntity.id in ?1")
    void deleteWithIds(List<UUID> assessmentIds);

}