package com.pusp.pusp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;

import com.pusp.pusp.Entities.Activity;
import com.pusp.pusp.Entities.Project;
import com.pusp.pusp.Entities.Clockin;
import com.pusp.pusp.Entities.Role;
import com.pusp.pusp.Entities.User;

@Repository
public interface ClockinsRepository extends JpaRepository<Clockin, String> {
    List<Clockin> findByRole(Role role);
    List<Clockin> findByActivity(Activity activity);
    List<Clockin> findByUser(User user);
    List<Clockin> findByProject(Project project);
    List<Clockin> findByProjectAndUser(Project project, User user);
    List<Clockin> findByStartAfterAndEndBeforeAndUserAndProject(Timestamp startDate, Timestamp endDate, User user, Project project);
    List<Clockin> findByStartAfterAndEndBeforeAndProject(Timestamp startDate, Timestamp endDate, Project project);
    List<Clockin> findByStartAfterAndEndBeforeAndUser(Timestamp startDate, Timestamp endDate, User user);
    List<Clockin> findByStartAfterAndEndBefore(Timestamp startDate, Timestamp endDate);
    List<Clockin> findByClockinSignerIsNull();
    List<Clockin> findByClockinSignerIsNullAndProject(Project project);
}
