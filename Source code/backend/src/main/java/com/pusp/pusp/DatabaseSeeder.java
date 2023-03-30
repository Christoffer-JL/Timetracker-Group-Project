package com.pusp.pusp;

import java.sql.Timestamp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

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

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private ActivitiesRepository activitiesRepository;
    private ClockinsRepository clockinsRepository;
    private ProjectsRepository projectsRepository;
    private RolesRepository rolesRepository;
    private UsersRepository usersRepository;    
    

    @Autowired
    public DatabaseSeeder(UsersRepository usersRepository, ActivitiesRepository activitiesRepository, ClockinsRepository clockinsRepository, ProjectsRepository projectsRepository, RolesRepository rolesRepository) {
        this.usersRepository = usersRepository;
        this.activitiesRepository = activitiesRepository;
        this.clockinsRepository = clockinsRepository;
        this.rolesRepository = rolesRepository;
        this.projectsRepository = projectsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // create some users and save them to the database
        try {
            
            usersRepository.save(new User("admin@admin.se", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode("password"), true, "Viktor", "0702562176"));
            usersRepository.save(new User("user@user.se", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode("userPassword"), false, "Philip", "0764371176"));
            usersRepository.save(new User("designer@design.dk", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode("d3s!gn#123"), false, "Bertil", "0736649021"));

            User viktor = usersRepository.findByEmail("admin@admin.se");
            User philip = usersRepository.findByEmail("user@user.se");
            User bertil = usersRepository.findByEmail("designer@design.dk");
            
            System.out.println(viktor.toString());
            System.out.println(philip.toString());
            System.out.println(bertil.toString());
            
            try {
                projectsRepository.save(new Project("TestProject"));
                projectsRepository.save(new Project("MegaProject"));
            } catch (Exception e) {}

            //create projects
            Project testProject = projectsRepository.findByName("TestProject");
            Project megaProject = projectsRepository.findByName("MegaProject");
            
            System.out.println(testProject.toString());
            System.out.println(megaProject.toString());

            //create activities
            Activity activitySDDD = activitiesRepository.save(new Activity("Utveckling av SDDD", testProject, "30"));
            Activity activitySDP = activitiesRepository.save(new Activity("Utveckling av SDP", megaProject, "10"));
            Activity activityDesign = activitiesRepository.save(new Activity("Designarbete", megaProject, "20"));
            Activity activityTestning = activitiesRepository.save(new Activity("Testningsarbete", megaProject, "50"));
            
            //create roles
            Role utvecklare = rolesRepository.save(new Role("Utvecklare", false, testProject));
            Role projectledare = rolesRepository.save(new Role("Projektledare", true, megaProject));
            Role designer = rolesRepository.save(new Role("Designer", false, megaProject));
            Role tester = rolesRepository.save(new Role("Testare", false, megaProject));
            
            viktor.addRole(utvecklare);
            viktor.addRole(projectledare);
            usersRepository.save(viktor);

            philip.addRole(projectledare);
            usersRepository.save(philip);

            bertil.addRole(designer);
            bertil.addRole(tester);
            usersRepository.save(bertil);


            //create clockins
            clockinsRepository.save(new Clockin(new Timestamp(System.currentTimeMillis()),  //start
                                                new Timestamp(System.currentTimeMillis() + 45 * 60 * 1000), //end
                                                "SDP",
                                                viktor,          //user
                                                projectledare,  //role
                                                activitySDP,    //activity
                                                megaProject));  //project

            clockinsRepository.save(new Clockin(new Timestamp(System.currentTimeMillis()),
                                                new Timestamp(System.currentTimeMillis() + 60 * 60 * 1000),
                                                "SDDD",
                                                philip,
                                                utvecklare,
                                                activitySDDD,
                                                testProject));

            clockinsRepository.save(new Clockin(new Timestamp(System.currentTimeMillis()),
                                                new Timestamp(System.currentTimeMillis() + 90 * 60 * 1000),
                                                "Design",
                                                philip,
                                                designer,
                                                activityDesign,
                                                megaProject));

                                                clockinsRepository.save(new Clockin(new Timestamp(System.currentTimeMillis()),
                                                new Timestamp(System.currentTimeMillis() + 90 * 60 * 1000),
                                                "Utveckling av SDP",
                                                bertil,
                                                designer,
                                                activitySDP,
                                                megaProject));

        } catch (Exception e) {
            System.out.println("\n\n\n\nSEEDING FAILED, RUN AGAIN IF YOU CAN'T LOGIN WITH THE ACCOUNT PROVIDED");
        }
        System.out.println("\n\nDEFAULT EMAIL: admin@admin.se \nDEFAULT PASSWORD: password \n\n\n\n");
    }
}
