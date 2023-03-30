package com.pusp.pusp.Services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.RolesRepository;
import com.pusp.pusp.Repositories.UsersRepository;

@Service
public class PrivilegedService {
   

    public boolean getIsPriviligedOrAdmin(Project project, User user) {
        if(user.getIsAdmin()) return true;

        boolean isPriviliged = false;

        List<Role> userRoles = new ArrayList<>(user.getRoles());
        
        for(Role role : userRoles){
            if(role.getProject().equals(project) && role.getIsPrivileged()) isPriviliged = true;
        }

       return isPriviliged;
    }

}
