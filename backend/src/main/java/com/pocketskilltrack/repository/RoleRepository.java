package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Role;
import com.pocketskilltrack.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
