package repositories;

import models.User;
import models.enums.Role;

import java.util.List;

public class UserRepository {

  public List<User> findAll() {

        return User.find.all();
  }

  public User findById(Long id) {

    return User.find.byId(id);
  }

  public User findByEmail(String email) {

    return User.find.query().where().eq("email", email).findOne();
  }

  public List<User> findHospitals() {

    return User.find.query().where().eq("role", Role.HOSPITAL).findList();
  }

  public List<User> findDonors() {

    return User.find.query().where().eq("role", "DONOR").findList();
  }

  public int countUsers() {

    return User.find.query().findCount();
  }

  public int countDonors() {

    return User.find.query().where().eq("role", "DONOR").findCount();
  }

  public int countHospitals() {

    return User.find.query().where().eq("role", "HOSPITAL").findCount();
  }

  public void save(User user) {

    user.save();
  }

  public void update(User user) {

    user.update();
  }

  public void delete(User user) {

    user.delete();
  }
}
