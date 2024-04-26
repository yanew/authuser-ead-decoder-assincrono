package com.ead.authuser.services;

import com.ead.authuser.enums.RoleType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {

	public Optional<RoleModel> findByRoleName(RoleType name);
	
}