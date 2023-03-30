package com.pusp.pusp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Project;

@Repository
public interface ActivitiesRepository extends JpaRepository<Activity, String> {
    List<Activity> findByProject(Project project);
    List<Activity> findByName(String name);
    List<Activity> findByCode(String name);
}
