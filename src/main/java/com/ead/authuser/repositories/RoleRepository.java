package com.ead.authuser.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ead.authuser.enums.RoleType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;

public interface RoleRepository extends JpaRepository<RoleModel, UUID>, JpaSpecificationExecutor<UserModel> {

	public Optional<RoleModel> findByRoleName(RoleType name);

}