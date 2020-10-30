/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.svalyn.application.entities.ProjectEntity;

@Repository
public interface IProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    boolean existsByLabel(String label);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProjectEntity projectEntity where projectEntity.id in ?1")
    void deleteWithId(List<UUID> projectIds);
}
