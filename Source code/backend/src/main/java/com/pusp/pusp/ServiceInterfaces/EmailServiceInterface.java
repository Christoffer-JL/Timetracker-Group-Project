package com.pusp.pusp.ServiceInterfaces;

import com.pusp.pusp.Entities.EmailDetails;

// Interface
public interface EmailServiceInterface {
 
    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);
 

}