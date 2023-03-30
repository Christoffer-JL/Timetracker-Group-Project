package com.pusp.pusp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    List<User> findByRoles(Role role);
}
