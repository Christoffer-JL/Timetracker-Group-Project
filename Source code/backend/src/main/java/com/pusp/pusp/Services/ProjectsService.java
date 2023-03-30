package com.pusp.pusp.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer.UserInfoEndpointConfig;
import org.springframework.stereotype.Service;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Clockin;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.ActivitiesRepository;
import com.pusp.pusp.Repositories.ClockinsRepository;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.RolesRepository;
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.ServiceInterfaces.ProjectsServiceInterface;

import jakarta.transaction.Transactional;


@Service
public class ProjectsService implements ProjectsServiceInterface{
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private ActivitiesRepository activitiesRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ClockinsRepository clockinsRepository;


    @Override
    public List<User> getUsers(Project project) {
        List<Role> rolesInProject = project.getRoles();

        List<User> usersInProject = new ArrayList<>();

        for (Role role : rolesInProject) {
            List<User> usersInRole = role.getUsers();
            usersInProject.addAll(usersInRole);
        }

       return usersInProject;
    }

    @Override
    public List<Project> getProjects(User user) {
        Set<Role> rolesList = user.getRoles();

        List<Project> projectList = new ArrayList<>();

        rolesList.forEach((role) -> {
            if(!projectList.contains(role.getProject())){
                projectList.add(role.getProject());
            }
                
        });

        return new ArrayList<Project>(projectList);

    }
    
    @Transactional
    public Project deleteProject(String projectId) {
        Project project = projectsRepository.findById(projectId).orElse(null);

        if (project != null) {
             // Iterate through the activities and delete related clockins
            for (Activity activity : project.getActivities()) {
                // Delete clockins associated with the activity
                clockinsRepository.deleteAll(activity.getClockins());
            }
            
            rolesRepository.deleteAll(project.getRoles());

    // Delete activities associated with the project
            activitiesRepository.deleteAll(project.getActivities());
            
            projectsRepository.delete(project);
            
            return project;
        }
        
        return null;
    }
}
