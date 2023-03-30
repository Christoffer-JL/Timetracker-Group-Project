package com.pusp.pusp.Controllers;

import java.time.Duration;
import java.time.Instant;
import java.time.chrono.MinguoEra;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.Services.PrivilegedService;
import com.pusp.pusp.Services.ProjectsService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class ProjectsController{
	
	@Autowired
	private ProjectsRepository projectsRepository;

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ProjectsService projectsService;
	
	@Autowired
	private ClockinsRepository clockinsRepository; 

	@Autowired
	private PrivilegedService privilegedService;

	@Autowired
	private RequestController requestController;

    @GetMapping("projects")
	List<Project> getAll(@RequestParam(name = "userId", required = false) String userId, @RequestParam(name = "deleted", required = false) String deleted, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (userId != null)	{
			Optional<User> optionalUser = usersRepository.findById(userId);
			
			if (optionalUser.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
			}
			
			if (deleted == null) {
				return projectsService.getProjects(optionalUser.get()).stream().filter(project -> !project.getDeleted()).toList();
			} else {
				return projectsService.getProjects(optionalUser.get());
			}
		}

		if(sessionUser.getIsAdmin()){
			if (deleted == null) {
				return projectsRepository.findAll().stream().filter(project -> !project.getDeleted()).toList();
		 	} else {
				return projectsRepository.findAll();
		 	}
		}

		return projectsService.getProjects(sessionUser);
    }

    @PostMapping("projects")
	Project create(@RequestBody projectDTO project, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		 
		User user = usersRepository.findById((String) session.getAttribute("userId")).get();

		if(!user.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att skapa ett nytt projekt.");
		}
		return projectsRepository.save(new Project(project.name));
	}

    @PutMapping("projects/{id}")
	Project update(@RequestBody projectDTO project, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Project> optionalProject = projectsRepository.findById(id);
		User user = usersRepository.findById((String) session.getAttribute("userId")).get();
		
		if (optionalProject.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if(!privilegedService.getIsPriviligedOrAdmin(optionalProject.get(), user)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att uppdatera ett projekt.");
		}
		
		Project currentProject = optionalProject.get();
		
		currentProject.setName(project.name);
		
		return projectsRepository.save(currentProject);
	}

	@GetMapping("projects/{id}")
	Project get(@PathVariable("id") String id) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Project> optionalProject = projectsRepository.findById(id);
		
		if (optionalProject.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		return optionalProject.get();
	}

    @DeleteMapping("projects/{id}")
	Project delete(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		User user = usersRepository.findById((String) session.getAttribute("userId")).get();
		Optional<Project> optionalProject = projectsRepository.findById(id);

		if (optionalProject.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if(!privilegedService.getIsPriviligedOrAdmin(optionalProject.get(), user)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att radera detta projektet.");
		}

		Project project = optionalProject.get();
		project.setDeleted(true);
		projectsRepository.save(project);
		
		return project; 
	}

	@GetMapping("projects/statistics/{id}")
	StatsDTO statistics(@PathVariable("id") String id) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Project> optionalProject = projectsRepository.findById(id);
		
		if (optionalProject.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}
		
		List<Clockin> allClockins = clockinsRepository.findByProject(optionalProject.get());

		int sumOfMinutes = 0;
		List<RoleStats> roleStats = new ArrayList<>();	
		List<ActivityStats> activityStats = new ArrayList<>();	
		List<UserStats> userStats = new ArrayList<>();	
		
		for (Clockin clockin : allClockins) {
			Instant instant1 = clockin.getStart().toInstant();
			Instant instant2 = clockin.getEnd().toInstant();
			
			int minutes = (int) Math.floor(Duration.between(instant1, instant2).toMinutes());
			
			sumOfMinutes += minutes;
			
			boolean activityFound = false;
			for( ActivityStats activity : activityStats ) {
				if (activity.id == clockin.getActivity().getId()) {
					activity.time += minutes;
					activityFound = true;
				}
			}
			
			if (!activityFound) {
				ActivityStats stat = new ActivityStats();
				stat.id = clockin.getActivity().getId();
				stat.name = clockin.getActivity().getName();
				stat.time = minutes;
				activityStats.add(stat);
			}

			boolean userFound = false;
			for ( UserStats user : userStats ) {
				if (user.id == clockin.getUser().getId()) {
					user.time += minutes;
					userFound = true;
				}
			}
			
			if (!userFound) {
				UserStats stat = new UserStats();
				stat.id = clockin.getUser().getId();	
				stat.name = clockin.getUser().getName();
				stat.time = minutes;
				userStats.add(stat);
			}
			
			boolean roleFound = false;
			for( RoleStats role : roleStats ) {
				if (role.id == clockin.getRole().getId()) {
					role.time += minutes;
					roleFound = true;
				}
			}
			
			if(!roleFound) {
				RoleStats stat = new RoleStats();
				stat.id = clockin.getRole().getId();
				stat.name = clockin.getRole().getName();
				stat.time = minutes;
				roleStats.add(stat);
			}
		}
		
		StatsDTO stats = new StatsDTO();
		stats.activities = activityStats;
		stats.roles = roleStats;
		stats.users = userStats;
		stats.totalTime = sumOfMinutes;

		return stats;
	}

	private static class StatsDTO {
		public List<RoleStats> roles;
		public List<ActivityStats> activities;
		public List<UserStats> users;
		public int totalTime;
	}
	
	private static class RoleStats {
		public String id;	
		public String name;
		public int time;
	}

	private static class ActivityStats {
		public String id;	
		public String name;
		public int time;
	}
	
	private static class UserStats {
		public String id;	
		public String name;
		public int time;
	}

	private static class projectDTO{
		public String name;
	}
	
}