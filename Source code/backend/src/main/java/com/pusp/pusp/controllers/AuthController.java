package com.pusp.pusp.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.pusp.pusp.Entities.User;
import com.pusp.pusp.Repositories.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@SessionAttributes({"userId", "isAdmin"})
public class AuthController {
    
   @Autowired
   private UsersRepository usersRepository;
   
   @PostMapping("/auth/login")
   User login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
    HttpSession session = request.getSession();

    
    User user;
    
    try {
        user = usersRepository.findByEmail(loginDTO.email);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren finns ej");
        }
    }
    catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Användaren finns ej");
    }
    
    if (Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().matches(loginDTO.password, user.getPassword())) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("isAdmin", user.getIsAdmin());
        return user;
    } 

    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Fel lösenord");
   }
   
   @PostMapping("/auth/logout") 
   String logout(HttpServletRequest request) {
    HttpSession session = request.getSession();
    session.invalidate();

    return "Logged out";
   }


   public static class LoginDTO {
    public LoginDTO() {}    

    public String email;
    public String password;
   }
}
