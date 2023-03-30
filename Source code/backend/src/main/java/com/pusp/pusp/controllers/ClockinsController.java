package com.pusp.pusp.Controllers;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties.Decryption;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer.UserInfoEndpointConfig;
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
import com.pusp.pusp.Repositories.ActivitiesRepository;
import com.pusp.pusp.Repositories.ClockinsRepository;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.RolesRepository;
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.Services.ProjectsService;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class ClockinsController{
	private ActivitiesRepository activitiesRepository;
    private ClockinsRepository clockinsRepository;
    private ProjectsRepository projectsRepository;
    private RolesRepository rolesRepository;
    private UsersRepository usersRepository;	

	@Autowired
	ProjectsService projectsService;

	@Autowired
    public ClockinsController(UsersRepository usersRepository, ActivitiesRepository activitiesRepository, ClockinsRepository clockinsRepository, ProjectsRepository projectsRepository, RolesRepository rolesRepository) {
		this.usersRepository = usersRepository;
        this.activitiesRepository = activitiesRepository;
        this.clockinsRepository = clockinsRepository;
        this.rolesRepository = rolesRepository;
        this.projectsRepository = projectsRepository;
    }

	@Autowired
	private RequestController requestController;
	
	Long offset = 0L;
	
    
    @GetMapping("clockins")
    List<Clockin> getAll(@RequestParam(name = "userId", required = false) String userId, @RequestParam(name = "start", required = false) Long start, 
						 @RequestParam(name = "end", required = false) Long end, @RequestParam(name = "projectId", required = false) String projectId, HttpSession session) {
			
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}	 
		
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		User user = null;
		Project project = null;
		
		if (userId != null) {
			Optional<User> optionalUser = usersRepository.findById(userId);
			
			if (optionalUser.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
			}
			
			user = optionalUser.get();
		}
		
		if (projectId != null) {
			Optional<Project> optionalProject = projectsRepository.findById(projectId);
			
			if (optionalProject.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
			}

			if(!projectsService.getUsers(optionalProject.get()).contains(sessionUser) && !sessionUser.getIsAdmin() ){
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att få tillgång till clockins i detta projektet");
			}
			
			project = optionalProject.get();
		}

		
		if (end != null && start != null) {
			if(projectId != null && userId != null) {
				return clockinsRepository.findByStartAfterAndEndBeforeAndUserAndProject(new Timestamp(start + offset), new Timestamp(end + offset), user, project);
			}
			
			if (projectId != null) {
				return clockinsRepository.findByStartAfterAndEndBeforeAndProject(new Timestamp(start + offset), new Timestamp(end + offset), project);
			}
			
			if (userId != null) {
				return clockinsRepository.findByStartAfterAndEndBeforeAndUser(new Timestamp(start + offset), new Timestamp(end + offset), user);
			}
		}
		
		if(projectId != null && userId != null) {
			return clockinsRepository.findByProjectAndUser(project, user);
		}
		
		if (projectId != null) {
			return clockinsRepository.findByProject(project);
		}
		
		if (userId != null) {
			return clockinsRepository.findByUser(user);
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att få tillgång till alla clockins");

		}
		
        return clockinsRepository.findAll();
    }
	
	@GetMapping("clockins/unsigned")
    List<Clockin> getUnsigned(@RequestParam(name = "projectId", required = false) String projectId, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (projectId != null) {
			Optional<Project> project = projectsRepository.findById(projectId);

			if (project.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
			}

			if(!projectsService.getUsers(project.get()).contains(sessionUser) && !sessionUser.getIsAdmin() ){
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att få tillgång till unsigned clockins i detta projektet");
			}
			
			return clockinsRepository.findByClockinSignerIsNullAndProject(project.get());
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att få tillgång till alla unsigned clockins");
		}
		
		return clockinsRepository.findByClockinSignerIsNull();
	}

    @PostMapping("clockins")
	Clockin create(@RequestBody createDTO clockin, HttpServletRequest request) {
    	HttpSession session = request.getSession();
		
		if (session.getAttribute("userId") == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Du måste vara inloggad för att detta");
		}

		User user = usersRepository.findById((String) session.getAttribute("userId")).get();
		
		if(clockin.start > clockin.end){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starttiden är efter sluttiden");
		}

		Optional<Activity> activity = activitiesRepository.findById(clockin.activityId);

		if (activity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktiviteten kunde inte hittas");
		}

		Optional<Project> project = projectsRepository.findById(clockin.projectId);

		if (project.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}
		
		List<Role> roles = rolesRepository.findByProjectAndUsers(project.get(), user);
			
		if (roles.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rollen kunde inte hittas");
		}

		Clockin createClockin = new Clockin(new Timestamp(clockin.start + offset), 
											new Timestamp(clockin.end + offset),
											clockin.description,
											user,          
											roles.get(0),  
											activity.get(),    
											project.get());  
		
		return clockinsRepository.save(createClockin);
	}

    @PutMapping("clockins/{id}")
	Clockin update(@RequestBody updateDTO clockin, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Clockin> optionalClockin = clockinsRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		
		if (optionalClockin.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clockin kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin() && !optionalClockin.get().getUser().equals(sessionUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att uppdatera denna clockin");
		} 

		if(clockin.start > clockin.end){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starttiden är efter sluttiden");
		}
		
		Clockin currentClockin = optionalClockin.get();
		Activity activity = activitiesRepository.findById(clockin.activityId).get();

		currentClockin.setActivity(activity);
        currentClockin.setStart(new Timestamp(clockin.start + offset));
        currentClockin.setEnd(new Timestamp(clockin.end + offset));
        currentClockin.setDescription(clockin.description);

		return clockinsRepository.save(currentClockin);
	}

	@GetMapping("clockins/{id}")
	Clockin get(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Clockin> optionalClockin = clockinsRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		
		if (optionalClockin.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clockin kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin() && !optionalClockin.get().getUser().equals(sessionUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna att se denna clockin");
		} 

		return optionalClockin.get();
	}

    @DeleteMapping("clockins/{id}")
	String delete(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<Clockin> optionalClockin = clockinsRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();


		if (optionalClockin.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clockin kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin() && !optionalClockin.get().getUser().equals(sessionUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna att radera denna clockin");
		} 
		

		clockinsRepository.delete(optionalClockin.get());
		
		return "deleted";
	}
	
	@PutMapping("clockins/{id}/sign")
	Clockin sign(@PathVariable("id") String clockinId, HttpServletRequest request) {
    	if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		HttpSession session = request.getSession();

		Optional<User> user = usersRepository.findById((String) session.getAttribute("userId"));
		
		if (!user.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användare kunde inte hittas");
		}
		
		Optional<Clockin> optionalClockin = clockinsRepository.findById(clockinId);
		
		if (!optionalClockin.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clockin kunde inte hittas");
		}
		
		Clockin clockin = optionalClockin.get();
		
		clockin.setClockinSigner(user.get());
		clockinsRepository.save(clockin);
		
		return clockin;
	}
	
	private static class updateDTO {
		public Long start;
      	public Long end;
      	public String description;
      	public String activityId;
	}	

	private static class createDTO {
		public Long start;
      	public Long end;
      	public String description;
      	public String activityId;
		public String projectId;
	}	



}