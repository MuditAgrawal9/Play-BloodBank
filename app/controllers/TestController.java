package controllers;

import models.*;
import play.mvc.*;

public class TestController extends Controller {

    public Result testEbean() {

        User user = new User();

        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("123");

        user.save();

        return ok("Saved");
    }

    public Result createDonor() {

        User user = User.find.byId(1L);

        Donor donor = new Donor();

        donor.setUser(user);

        donor.setBloodGroup("A+");

        donor.setAge(22);

//        donor.setPhone("9999999999");
//
//        donor.setCity("Varanasi");

        donor.save();

        return ok("Donor Saved");
    }

    public Result viewDonor() {

        Donor donor =
                Donor.find.query()
                        .where()
                        .eq("user.id", 1L)
                        .findOne();

        if(donor == null) {
            return ok("Donor not found");
        }

        return ok(
                donor.getBloodGroup()
                        + " "
//                        + donor.getCity()
        );
    }

    public Result seedInventory() {

        String[] groups = {
                "A+","A-",
                "B+","B-",
                "AB+","AB-",
                "O+","O-"
        };

        for(String group : groups) {

            BloodInventory item =
                    new BloodInventory();

            item.setBloodGroup(group);

            item.setUnitsAvailable(10);

            item.save();
        }

        return ok("Inventory Seeded");
    }

    public Result createRequest() {

        User user =
                User.find.byId(1L);

        BloodTransaction transaction =
                new BloodTransaction();

        transaction.setUser(user);

        transaction.setBloodGroup("A+");

        transaction.setUnits(2);

        transaction.setStatus("PENDING");

        transaction.save();

        return ok("Request Saved");
    }
}