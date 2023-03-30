package com.pusp.pusp.Entities;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;

@Entity(name="Roles")
public class Role {

  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private String id;
  private String name;
  private boolean isPrivileged;
  private Boolean deleted = Boolean.FALSE;

  @UpdateTimestamp
  private Timestamp updatedAt;
  @CreationTimestamp
  private Timestamp createdAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "projectId", nullable = false)
  @JsonBackReference
  private Project project;
  
  @ManyToMany(mappedBy = "roles")
  @JsonManagedReference
  List<User> users;
  
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
  @JsonBackReference
  @JsonIgnore()
  private List<Clockin> clockins;
  
  protected Role() {}

  public Role(String name, boolean isPrivileged, Project project) {
    this.name = name;
    this.isPrivileged = isPrivileged;
    this.project = project;
  }
  
  @Override
  public String toString() {
    return String.format(
        "Role[name='%s', isPrivileged='%s', projectName='%s']",
        name, isPrivileged, project.getName());
  }
  
  public String getId() {
    return id;
  }

  public boolean getIsPrivileged(){
    return isPrivileged;
  }

  public void setIsPrivileged(boolean priviligedBool){
    this.isPrivileged = priviligedBool;
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

  public Project getProject() {
    return project;
  }
  
  public void setProject(Project project) {
    this.project = project;
  }

  public String getProjectName(){
    return project.getName();
  }

  public List<User> getUsers(){
    return this.users;
  }

  public void addUser(User user){
    this.users.add(user);
  }

  public void removeUser(User user){
    this.users.remove(user);
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }
  
  public Timestamp getUpdatedAt() {
    return createdAt;
  }
}