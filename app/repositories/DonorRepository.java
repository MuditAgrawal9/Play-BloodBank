package repositories;

import models.Donor;

import java.util.List;

public class DonorRepository {

  public Donor findById(Long id) {
    return Donor.find.byId(id);
  }

  public List<Donor> findAll() {
    return Donor.find.all();
  }

  public Donor findByUserId(Long userId) {
    return Donor.find.query().where().eq("user.id", userId).findOne();
  }

  public void update(Donor donor) {
    donor.update();
  }
}
