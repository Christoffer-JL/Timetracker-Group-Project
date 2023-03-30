package com.pusp.pusp.ServiceInterfaces;

import java.util.List;

import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.User;

public interface ProjectsServiceInterface {
   public abstract List<User> getUsers(Project project);
   public abstract List<Project> getProjects(User user);
}