package models;

import io.ebean.Model;
import io.ebean.Finder;

import jakarta.persistence.*;

@Entity
@Table(name = "donors")
public class Donor extends Model {

  @Id private Long id;

  @OneToOne private User user;

  private String bloodGroup;

  private Integer age;

  public static final Finder<Long, Donor> find = new Finder<>(Donor.class);

  // Getters & Setters

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getBloodGroup() {
    return bloodGroup;
  }

  public Integer getAge() {
    return age;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setBloodGroup(String bloodGroup) {
    this.bloodGroup = bloodGroup;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
