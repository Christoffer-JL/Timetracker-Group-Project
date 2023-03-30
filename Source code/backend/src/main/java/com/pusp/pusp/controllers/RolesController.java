package com.pusp.pusp.Controllers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpsRedirectSpec;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Clockin;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.ClockinsRepository;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.RolesRepository;
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.Services.PrivilegedService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpSession;

@RestController
public class RolesController{
	
	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private ProjectsRepository projectsRepository;

	@Autowired
	private ClockinsRepository clockinsRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private PrivilegedService privilegedService;

	@Autowired
	private RequestController requestController;

    
    @GetMapping("roles")
    List<Role> getAll(@RequestParam(name = "projectId", required = false) String projectId, @RequestParam(name = "userId", required = false) String userId, @RequestParam(name = "deleted", required = false) String delete, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		
		if (userId != null) {
			Optional<User> optionalUser = usersRepository.findById(userId);
			
			if (optionalUser.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
			}

			if(!sessionUser.getIsAdmin() && !optionalUser.get().equals(sessionUser)){
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att se rollerna för denna användare");
			}
			
			if(delete == null) {
				return rolesRepository.findByUsers(optionalUser.get()).stream().filter(role -> !role.getDeleted()).toList();
			} else {
				return rolesRepository.findByUsers(optionalUser.get());
			}
			
		}


		if (projectId != null) {
			Optional<Project> optionalProject = projectsRepository.findById(projectId);
			
			if (optionalProject.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
			}

			if(!sessionUser.getIsAdmin() && !privilegedService.getIsPriviligedOrAdmin(optionalProject.get(), sessionUser)){
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att se rollerna för detta projekt");

			}
			
			if(delete == null) {
				return rolesRepository.findByProject(optionalProject.get()).stream().filter(role -> !role.getDeleted()).toList();
			} else {
				return rolesRepository.findByProject(optionalProject.get());
			}
		}

	    if(delete == null) {
			if(!sessionUser.getIsAdmin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att se alla roller");

    		return rolesRepository.findAll().stream().filter(role -> !role.getDeleted()).toList();
	    } else {

			if(!sessionUser.getIsAdmin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att se alla roller");
    		return rolesRepository.findAll();
	    }
    }

    @PostMapping("roles")
	Role create(@RequestBody createRole role, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		Optional<Project> project = projectsRepository.findById(role.projectId);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (!project.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att skapa nya roller i detta projektet");
		}
		
		Role createRole = new Role(role.name, role.isPrivileged, project.get());

		return rolesRepository.save(createRole);
	}
	
	@GetMapping("roles/{id}")
	Role get(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Role> optionalRole = rolesRepository.findById(id);
		

		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalRole.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalRole.get().getProject().getId());

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att se denna rollen");
		}

		return optionalRole.get();
	}

    @PutMapping("roles/{id}")
	Role update(@RequestBody updateRoleDTO role, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Role> optionalRole = rolesRepository.findById(id);

		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();


		if (optionalRole.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Role currentRole = optionalRole.get();

		Optional<Project> project = projectsRepository.findById(role.projectId);

		if (project.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att uppdatera denna rollen");
		}
		
        currentRole.setIsPrivileged(role.isPrivileged);
        currentRole.setName(role.name);
        currentRole.setProject(project.get());

		return rolesRepository.save(currentRole);
	}

	

    @DeleteMapping("roles/{id}")
	Role delete(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Role> optionalRole = rolesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();
		
		if (optionalRole.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalRole.get().getProject().getId());

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att radera denna rollen");
		}

		List<Clockin> clockinList = clockinsRepository.findByRole(optionalRole.get());

		for (Clockin clockin : clockinList) {
			clockin.setRole(null);
			clockinsRepository.save(clockin);
		}

		List<User> userList = usersRepository.findByRoles(optionalRole.get());

		for (User user : userList) {
			user.removeRole(optionalRole.get());
			usersRepository.save(user);
		}
		
		Role role = optionalRole.get();
		role.setDeleted(true);

		rolesRepository.save(role);
		
		return role;
	}

	@PutMapping("roles/{id}/add")
	String add(@RequestBody() userDTO userforId, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> optionalUser = usersRepository.findById(userforId.userId);
		Optional<Role> optionalRole = rolesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();
		

		if (optionalUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(optionalRole.isEmpty()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalRole.get().getProject().getId()); 

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att lägga till en roll till en användare i detta projektet");
		}
		
		User user = optionalUser.get();

		if(user.getRoles().contains(optionalRole.get())){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Användaren har redan denna roll");
		}
		
		if (rolesRepository.findByProjectAndUsers(optionalRole.get().getProject(), user).size() > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Användaren har redan en roll i projektet");
		}

		user.addRole(optionalRole.get());
		usersRepository.save(user);

		return "Role assigned successfully";
	}

	@PutMapping("roles/{id}/remove")
	String remove(@RequestBody userDTO userforId, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> optionalUser = usersRepository.findById(userforId.userId);
		Optional<Role> optionalRole = rolesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(optionalRole.isEmpty()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalRole.get().getProject().getId()); 


		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att lägga till en roll till en användare i detta projektet");
		}

		User user = optionalUser.get();

		if(!user.getRoles().contains(optionalRole.get())){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Användaren har inte denna roll");
		}

		user.removeRole(optionalRole.get());
		usersRepository.save(user);
		
		return "Role removed successfully";
	}

	private static class updateRoleDTO{
		public boolean isPrivileged;
		public String name;
		public String projectId;
	}

	private static class createRole{
		public String name;
		public boolean isPrivileged;
		public String projectId;
	}

	private static class userDTO{
		public String userId;
	}

	
}