package com.pusp.pusp.Services;



import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailService {

    @Autowired
    private SendGrid sendGrid;

    public void sendEmail(String to, String password) throws IOException {
        Email fromEmail = new Email("pusp2023hbg@gmail.com");
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/html", "Här kommer ditt nya lösenord: " + password);
        Mail mail = new Mail(fromEmail, "lösenordsuppdatering", toEmail, emailContent);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        // Optionally, you can check the response status code and body
        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());
    }
} 