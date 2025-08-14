package com.study.ticketservice.domain.auth.repository;

import com.study.ticketservice.domain.auth.entity.RolePrivilegeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RolePrivilegeMapRepository extends JpaRepository<RolePrivilegeMap, Long> {
    @Query("select distinct p.name from RolePrivilegeMap map join map.privilege p where map.role.roleId in :roleIds")
    List<String> findPrivilegeNamesByRoleIds(@Param("roleIds") Collection<Long> roleIds);
}


