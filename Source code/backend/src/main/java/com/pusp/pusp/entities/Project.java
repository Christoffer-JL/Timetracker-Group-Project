package com.pusp.pusp.Entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity(name="Projects")
public class Project {
  
  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private String id;

  @Column(unique=true)
  private String name;

  private Boolean deleted = Boolean.FALSE;
  
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
  @JsonBackReference
  private List<Activity> activities;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
  @JsonManagedReference
  private List<Role> roles;
  
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
  @JsonManagedReference
  @JsonIgnore()
  private List<Clockin> clockins;

  @UpdateTimestamp
  private Timestamp updatedAt;
  @CreationTimestamp
  private Timestamp createdAt;
  
  protected Project() {}

  public Project(String name) {
      this.name = name;
  }

  @Override
  public String toString() {
    return String.format(
        "Project[id='%s', name='%s']",
        id, name);
  }
  
  public String getId(){
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
  
  public List<Activity> getActivities() {
    return activities;
  }

  public void addActivity(Activity activities){
    this.activities.add(activities);
  }

  public void removeActivity(Activity activity){
    this.activities.remove(activity);
  }

  public List<Role> getRoles(){
    return roles;
  }
  
  public void addRole(Role role){
    this.roles.add(role);
  }
  
  public List<Clockin> getClockins() {
    return clockins;
  }
  
  public void addClockin(Clockin clockin) {
    this.clockins.add(clockin);
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }
  
  public Timestamp getUpdatedAt() {
    return createdAt;
  }
}
