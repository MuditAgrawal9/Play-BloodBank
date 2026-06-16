package controllers;

import models.Donor;
import models.User;
import play.mvc.*;

import java.util.Map;

public class DonorController extends Controller {

    public Result profile(Http.Request request) {

        String email =
                request.session()
                        .getOptional("email")
                        .orElse(null);

        if(email == null) {
            return redirect("/login");
        }

        User user =
                User.find.query()
                        .where()
                        .eq("email", email)
                        .findOne();

        Donor donor =
                Donor.find.query()
                        .where()
                        .eq("user.id", user.getId())
                        .findOne();

        return ok(
                views.html.profile.render(donor)
        );
    }

    public Result saveProfile(Http.Request request) {

        String email =
                request.session()
                        .getOptional("email")
                        .orElse(null);

        if(email == null) {
            return redirect("/login");
        }

        Map<String,String[]> data =
                request.body().asFormUrlEncoded();

        String bloodGroup =
                data.get("bloodGroup")[0];

        String age =
                data.get("age")[0];

        String phone =
                data.get("phone")[0];

        String city =
                data.get("city")[0];

        String address =
                data.get("address")[0];

        User user =
                User.find.query()
                        .where()
                        .eq("email", email)
                        .findOne();

        if(user == null) {
            return unauthorized("User not found");
        }

        Donor donor =
                Donor.find.query()
                        .where()
                        .eq("user.id", user.getId())
                        .findOne();

        if(donor == null) {

            donor = new Donor();

            donor.setUser(user);

        }

        donor.setBloodGroup(bloodGroup);

        donor.setAge(
                Integer.parseInt(age)
        );

//        System.out.println("Hello" + address);

        user.setAddress(address);
        user.setCity(city);
        user.setPhone(phone);
        user.update();

        if(donor.getId() == null) {
            donor.save();
            return ok("Profile Saved");
        } else {
            donor.update();
        }

        return ok("Profile Updated");
    }

}