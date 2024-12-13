package com.techstud.sch_auth.repository;

import com.techstud.sch_auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Long, Role> {

}
