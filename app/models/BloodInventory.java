package models;

import io.ebean.Model;
import io.ebean.Finder;

import jakarta.persistence.*;

@Entity
@Table(name = "blood_inventory")
public class BloodInventory extends Model {

    @Id
    private Long id;

    private String bloodGroup;

    private Integer unitsAvailable;

    public static final Finder<Long, BloodInventory> find =
            new Finder<>(BloodInventory.class);

    public Long getId() {
        return id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Integer getUnitsAvailable() {
        return unitsAvailable;
    }

    public void setUnitsAvailable(Integer unitsAvailable) {
        this.unitsAvailable = unitsAvailable;
    }
}