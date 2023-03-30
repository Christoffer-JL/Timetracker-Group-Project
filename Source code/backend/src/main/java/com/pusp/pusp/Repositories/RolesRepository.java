package com.pusp.pusp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;

@Repository
public interface RolesRepository extends JpaRepository<Role, String> {
    List<Role> findByName(String name);
    List<Role> findByProject(Project project);
    List<Role> findByUsers(User user);
	List<Role> findByProjectAndUsers(Project project, User user);
}
