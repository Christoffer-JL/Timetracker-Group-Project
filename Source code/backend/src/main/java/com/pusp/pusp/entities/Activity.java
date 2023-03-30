package com.pusp.pusp.Entities;

import java.sql.Timestamp;
import java.util.List;

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
import jakarta.persistence.OneToMany;

@Entity(name="Activities")
public class Activity {

  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private String id;

  private String name;
  private String code;
  private Boolean deleted = Boolean.FALSE;

  @UpdateTimestamp
  private Timestamp updatedAt;
  @CreationTimestamp
  private Timestamp createdAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "projectId", nullable = false)
  @JsonManagedReference
  private Project project;
  
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "activity")
  @JsonBackReference
  private List<Clockin> clockins; 

  protected Activity() {}

  public Activity(String name, Project project, String code) {
    this.name = name;
    this.project = project;
    this.code = code;
  }

  @Override
  public String toString() {
    return String.format(
        "Project[name='%s', projectName='%s', code='%s']",
        name, this.project.getName(), this.code);
  }
  
  public String getId() {
    return id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }
  
  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
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
  
  public List<Clockin> getClockins() {
    return clockins;
  }
  
  public Timestamp getCreatedAt() {
    return createdAt;
  }
  
  public Timestamp getUpdatedAt() {
    return createdAt;
  }
}
