package com.pusp.pusp.Controllers;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusp.pusp.Entities.Clockin;
import com.pusp.pusp.Entities.EmailDetails;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.ClockinsRepository;
import com.pusp.pusp.Repositories.ProjectsRepository;
import com.pusp.pusp.Repositories.UsersRepository;
import com.pusp.pusp.Services.MailService;
import com.pusp.pusp.Services.PrivilegedService;
import com.pusp.pusp.Services.ProjectsService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.http.HttpSession;

@RestController
public class UsersController {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ProjectsRepository projectsRepository;
	
	@Autowired
	private ClockinsRepository clockinsRepository;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private ProjectsService projectsService;

	@Autowired
	private RequestController requestController;

	
	@GetMapping("users")
	List<User> getAll(HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		return usersRepository.findAll();
	}	
	
	@PostMapping("users")
	User create(@RequestBody createDTO user, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna att skapa nya användare");
		}

		if(!validatePassword(user.password)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lösenordet uppnår inte kraven");
		}

		User createUser = new User(user.email, Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(user.password), user.isAdmin, user.name, user.phoneNumber);
		
		return usersRepository.save(createUser);
	}
	
	@GetMapping("users/{id}")
	User get(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}

		

		
		Optional<User> optionalUser;

		if(id.equals("@me")){
			optionalUser = usersRepository.findById((String) session.getAttribute("userId"));
		}
		else{
			optionalUser = usersRepository.findById(id);
		}
		
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}
		
		if(!sessionUser.getIsAdmin() && !optionalUser.get().equals(sessionUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har inte behörigheterna för att se denna användare");
		}
		
		return optionalUser.get();
	}
	
	@PutMapping("users/{id}")
	User update(@RequestBody updateDTO user, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> optionalUser = usersRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();

		if (optionalUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna att uppdatera användare");
		}
		
		User currentUser = optionalUser.get();
		
		currentUser.setEmail(user.email);
		currentUser.setIsAdmin(user.isAdmin);
		currentUser.setName(user.name);
		currentUser.setPhoneNumber(user.phoneNumber);
		
		
		return usersRepository.save(currentUser);
	}
	
	@DeleteMapping("users/{id}")
	User delete(@PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> user = usersRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();


		if (user.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna att ta bort användare");
		}

		usersRepository.delete(user.get());
		
		return user.get();
	}

	@PutMapping("users/{id}/reset")
	String resetPassword(@RequestBody String password, @PathVariable("id") String id, HttpSession session) throws JsonMappingException, JsonProcessingException{
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> optionalUser = usersRepository.findById(id);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();


		if(optionalUser.isEmpty()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(!sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna att byta lösenord");
		}


		if(!validatePassword(password)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lösenordet uppnår inte kraven");
		}

		User user = optionalUser.get();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(password);
		String newPassword = jsonNode.get("password").asText();

		System.out.println(newPassword);
		user.setPassword(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(newPassword));
		usersRepository.save(user);

		
		try {
			mailService.sendEmail(user.getEmail(), newPassword);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return "Email sent";
		
	}
	
	@GetMapping("users/{id}/statistics")
	StatsDTO getStats(@RequestParam(name = "projectId", required = true) String projectId, @PathVariable("id") String id, HttpSession session) {
		if(!requestController.bucket.tryConsume(1)){
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests(>100 per minute)");
		}
		
		Optional<User> optionalUser = usersRepository.findById(id);
		Optional<Project> optionalProject = projectsRepository.findById(projectId);
		User sessionUser = usersRepository.findById((String) session.getAttribute("userId")).get();


		if (optionalProject.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projektet kunde inte hittas");
		}

		if (optionalUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren kunde inte hittas");
		}

		if(!projectsService.getUsers(optionalProject.get()).contains(optionalUser.get()) && !sessionUser.getIsAdmin()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Du har inte behörigheterna för att se statistik för detta projekt");
		}

		List<Clockin> allClockins = clockinsRepository.findByProjectAndUser(optionalProject.get(), optionalUser.get());
		
		int sumOfMinutes = 0;
		List<ActivityStats> activityStats = new ArrayList<>();
		
		for (Clockin clockin : allClockins ) {
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
		}
		
		StatsDTO stats = new StatsDTO();
		stats.activities = activityStats;
		stats.totalTime = sumOfMinutes;
		
		return stats;
	}

	private static class StatsDTO {
		public List<ActivityStats> activities;
		public int totalTime;
	}
	
	private static class ActivityStats {
		public String id;	
		public String name;
		public int time;
	}
	
	private static class updateDTO{
		public String email;
		public boolean isAdmin;
		public String name;
		public String phoneNumber;
	}

	private static class createDTO{
		public String email;
		public String password;
		public boolean isAdmin;
		public String name;
		public String phoneNumber;
	}

	public static boolean validatePassword(String password) {
		if (password.length() < 6) {
			return false;
		}
		
		// Check for uppercase letter, special character, and digit
		boolean hasUppercase = false;
		boolean hasSpecialChar = false;
		boolean hasDigit = false;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c >= 65 && c <= 90) { // uppercase letter
				hasUppercase = true;
			} else if ((c >= 33 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96)) { // special character
				hasSpecialChar = true;
			} else if (c >= 48 && c <= 57) { // digit
				hasDigit = true;
			}
		}
		
		// Check if all requirements are met
		return hasUppercase && hasSpecialChar && hasDigit;
	}

}
