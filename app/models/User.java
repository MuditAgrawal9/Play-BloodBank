package models;

import io.ebean.Model;
import io.ebean.Finder;

import jakarta.persistence.*;
import models.enums.Role;

@Entity
@Table(name = "users")
public class User extends Model {

  @Id private Long id;

  private String name;

  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String phone;

  private String city;

  private String address;

  private String pincode;

  public static final Finder<Long, User> find = new Finder<>(User.class);

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getPhone() {
    return phone;
  }

  public String getCity() {
    return city;
  }

  public String getAddress() {
    return address;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPincode() {
    return pincode;
  }

  public void setPincode(String pincode) {
    this.pincode = pincode;
  }
}
