package com.pusp.pusp.Entities;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;

@Entity(name="Users")
public class User {

  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private String id;
  
  @Column(unique=true)
  private String email;
  
  @JsonIgnore
  private String password;

  private boolean isAdmin;
  private String name;
  private String phoneNumber;

  @UpdateTimestamp
  private Timestamp updatedAt;
  @CreationTimestamp
  private Timestamp createdAt;
  
  @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "User_Role", 
      joinColumns = @JoinColumn(name = "userId"), 
      inverseJoinColumns = @JoinColumn(name = "roleId")
  )
  @JsonBackReference
  Set<Role> roles;
  
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
  @JsonBackReference
  @JsonIgnore()
  private List<Clockin> clockins;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "clockinSigner")
  @JsonBackReference
  @JsonIgnore()
  private List<Clockin> signedClockins;
  
  protected User() {}

  public User(String email, String password, boolean isAdmin, String name, String phoneNumber) {
    this.email = email;
    this.isAdmin = isAdmin;
    this.password = password;
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String toString() {
    return String.format(
        "User[id='%s', name='%s' email='%s', isAdmin='%s']",
        id, name, email, isAdmin);
  }

  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getPhoneNumber(){
    return this.phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber){
    this.phoneNumber = phoneNumber;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public void setPassword(String password) {
      this.password = password;
  }
  
  public String getPassword() {
    return password;
  }

  public boolean getIsAdmin() {
    return isAdmin;
  }
  
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }
  
  public Timestamp getCreatedAt() {
    return createdAt;
  }
  
  public Timestamp getUpdatedAt() {
    return createdAt;
  }

  public Set<Role> getRoles(){
    return this.roles;
  }

  public void removeRole(Role role){
    this.roles.remove(role);
  }

  public void addRole(Role role){
    this.roles.add(role);
  }
}