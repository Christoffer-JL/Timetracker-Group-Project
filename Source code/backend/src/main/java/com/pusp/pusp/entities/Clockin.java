package com.pusp.pusp.Entities;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name="Clockins")
public class Clockin {

  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private String id;

  private Timestamp start;
  private Timestamp end;
  private String description;
  
  @UpdateTimestamp
  private Timestamp updatedAt;
  @CreationTimestamp
  private Timestamp createdAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userId", nullable = false)
  @JsonManagedReference
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "signedById", nullable = true)
  @JsonManagedReference
  private User clockinSigner;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "roleId", nullable = true)
  @JsonManagedReference
  private Role role;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "activityId", nullable = false)
  @JsonManagedReference
  private Activity activity;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "projectId", nullable = false)
  @JsonBackReference
  private Project project;


  protected Clockin() {}

  public Clockin(Timestamp start, Timestamp end, String description, User user, Role role, Activity activity, Project project) {
      this.start = start;
      this.end = end;
      this.description = description;
      this.clockinSigner = null;
      this.user = user;
      this.activity = activity;
      this.role = role;
      this.project = project;
  }

  @Override
  public String toString() {
    return String.format(
        "Clockin[start='%s', end='%s', description='%s', projectName='%s', role='%s', activity='%s']",
        start, end, description, project.getName(), role.getName(), activity.getName());
  }

  
  public String getId() {
    return id;
  }
  
  public Timestamp getStart() {
    return this.start;
  }

  public void setStart(Timestamp start) {
    this.start = start;
  }

  public Timestamp getEnd() {
    return this.end;
  }

  public void setEnd(Timestamp end){
    this.end = end;
  }

  public User getUser(){
    return user;
  }

  public User getClockinSigner(){
    return clockinSigner;
  }

  public void setClockinSigner(User clockinSigner){
    this.clockinSigner = clockinSigner;
  }

  public Role getRole(){
    return this.role;
  }

  public void setRole(Role role){
    this.role = role;
  }

  public Activity getActivity(){
    return this.activity;
  }

  public void setActivity(Activity activity){
    this.activity = activity;
  }

  public Project getProject(){
    return this.project;
  }

  public void setProject(Project project){
    this.project = project;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }
  
  public Timestamp getUpdatedAt() {
    return createdAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}