package repositories;

import models.BloodInventory;

import java.util.List;

public class InventoryRepository {

  public BloodInventory findByBloodGroup(String bloodGroup) {

    return BloodInventory.find.query().where().eq("bloodGroup", bloodGroup).findOne();
  }

  public void update(BloodInventory inventory) {
    inventory.update();
  }

  public List<BloodInventory> findAll() {
    return BloodInventory.find.all();
  }
}
