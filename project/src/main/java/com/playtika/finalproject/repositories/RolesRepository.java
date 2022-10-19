package com.playtika.finalproject.repositories;

import com.playtika.finalproject.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role,Long> {

    com.playtika.finalproject.models.Role getRoleByName(String name);
}
