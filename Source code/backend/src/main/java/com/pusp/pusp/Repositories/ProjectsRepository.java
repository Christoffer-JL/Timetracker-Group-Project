package com.pusp.pusp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pusp.pusp.Entities.Project;

@Repository
public interface ProjectsRepository extends JpaRepository<Project, String> {
    Project findByName(String name);
}