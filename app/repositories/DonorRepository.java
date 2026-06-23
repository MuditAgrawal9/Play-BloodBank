package repositories;

import models.Donor;

import java.util.List;

public class DonorRepository {

    public Donor findById(
            Long id
    ) {
        return Donor.find.byId(id);
    }

    public List<Donor> findAll() {
        return Donor.find.all();
    }
}