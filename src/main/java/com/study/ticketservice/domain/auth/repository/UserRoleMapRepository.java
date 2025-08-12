package com.study.ticketservice.domain.auth.repository;

import com.study.ticketservice.domain.auth.entity.UserRoleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleMapRepository extends JpaRepository<UserRoleMap, Long> {
    List<UserRoleMap> findAllByUserUserId(Long userId);

    @Query("select r.name from UserRoleMap urm join urm.role r where urm.user.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}