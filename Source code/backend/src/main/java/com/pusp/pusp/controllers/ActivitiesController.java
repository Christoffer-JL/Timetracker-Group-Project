package com.pusp.pusp.Controllers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.ActivitiesRepository;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.Services.PrivilegedService;
import com.pusp.pusp.Services.ProjectsService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpSession;

@RestController
public class ActivitiesController{
	
	@Autowired
	private ActivitiesRepository activitiesRepository;

	@Autowired
	private ProjectsRepository projectsRepository;

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ProjectsService projectsService;

	@Autowired
	private PrivilegedService privilegedService;

	@Autowired
	private RequestController requestController;



    @GetMapping("activities")
    List<Activity> getAll(@RequestParam(name = "projectId", required = false) String projectId, HttpSession session, @RequestParam(name = "deleted", required = false) String delete) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (projectId != null)	{
			Optional<Project> optionalProject = projectsRepository.findById(projectId);
			
			if (optionalProject.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
			}
			

			if(!projectsService.getUsers(optionalProject.get()).contains(sessionUser) && !sessionUser.getIsAdmin() ){
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att få tillgång till aktiviteter i detta projektet");
			} 

			if (delete == null) {
				return activitiesRepository.findByProject(optionalProject.get()).stream().filter(activity -> !activity.getDeleted()).toList();
			} else {
				return activitiesRepository.findByProject(optionalProject.get());
			}
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att se alla aktiviteter");
		}

		if (delete == null) {
       		return activitiesRepository.findAll().stream().filter(activity -> !activity.getDeleted()).toList();
		} else {
       		return activitiesRepository.findAll();
		}
    }

    @PostMapping("activities") 
	 Activity create(@RequestBody createDTO activityDetails, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		Optional<Project> project = projectsRepository.findById(activityDetails.projectId);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		
		if (!project.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att skapa nya aktiviteter i detta projektet");
		}
		
		Activity activity = new Activity(activityDetails.name, project.get(), activityDetails.code);

		return activitiesRepository.save(activity);
	}
	
	

    @PutMapping("activities/{id}")
	Activity update(@RequestBody updateDTO updateActivity, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Activity> optionalActivity = activitiesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalActivity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktiviteten kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalActivity.get().getProject().getId());

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att skapa nya aktiviteter i detta projektet");
		}
		
		Activity currentActivity = optionalActivity.get();
		
		currentActivity.setCode(updateActivity.code);
		currentActivity.setName(updateActivity.name);

		return activitiesRepository.save(currentActivity);
	}

	@GetMapping("activities/{id}")
	Activity get(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Activity> optionalActivity = activitiesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();
	

		if (optionalActivity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktiviteten kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalActivity.get().getProject().getId());

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att få tillgång till aktiviteter i detta projektet");
		}

		return optionalActivity.get();
	}


    @DeleteMapping("activities/{id}")
	Activity delete(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Activity> optionalActivity = activitiesRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalActivity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktiviteten kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(optionalActivity.get().getProject().getId());

		if(!privilegedService.getIsPriviligedOrAdmin(project.get(), sessionUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att radera aktiviteter i detta projektet");
		}
		
		Activity activity = optionalActivity.get();
		activity.setDeleted(true);

		activitiesRepository.save(activity);
		
		return activity;
	}

	private static class createDTO {
		public String name;
		public String projectId;
		public String code;
	}

	private static class updateDTO {
		public String name;
		public String code;
	}



}
