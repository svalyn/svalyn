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
import org.springframework.stereotype.Repository;

import com.svalyn.application.entities.DescriptionEntity;

@Repository
public interface IDescriptionRepository extends JpaRepository<DescriptionEntity, UUID> {
    List<DescriptionEntity> findAllByProjectId(UUID projectId);
}
