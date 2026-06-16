package controllers;

import models.BloodTransaction;
import models.User;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class RequestController extends Controller {

    public Result requestPage(Http.Request request) {

        String email =
                request.session()
                        .getOptional("email")
                        .orElse(null);

        if(email == null) {
            return redirect("/login");
        }

        return ok(
                views.html.request.render()
        );
    }

    public Result submitRequest(Http.Request request) {

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

        String unitsRequired =
                data.get("unitsRequired")[0];

        String hospitalName =
                data.get("hospitalName")[0];

        User user =
                User.find.query()
                        .where()
                        .eq("email", email)
                        .findOne();

        if(user == null) {
            return unauthorized("User not found");
        }

        BloodTransaction bloodTransaction =
                new BloodTransaction();

        bloodTransaction.setUser(user);

        bloodTransaction.setBloodGroup(
                bloodGroup
        );

        bloodTransaction.setUnits(
                Integer.parseInt(unitsRequired)
        );

        bloodTransaction.setStatus(
                "PENDING"
        );

        bloodTransaction.save();

        return ok("Blood Request Submitted");
    }

    public Result myRequests(Http.Request request) {

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

        List<BloodTransaction> requests =
                BloodTransaction.find.query()
                        .where()
                        .eq("user.id", user.getId())
                        .findList();

        return ok(
                views.html.myRequests.render(
                        requests
                )
        );
    }
}