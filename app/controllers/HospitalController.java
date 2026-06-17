package controllers;

import models.BloodTransaction;
import models.User;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class HospitalController extends Controller {

    public Result dashboard(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(role == null) {
            return redirect("/login");
        }

        if(!"HOSPITAL".equals(role)) {
            return forbidden("Access Denied");
        }

        return ok(
                views.html.hospital.render()
        );
    }

    public Result requestBloodPage(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"HOSPITAL".equals(role)) {
            return forbidden("Access Denied");
        }

        return ok(
                views.html.requestBlood.render()
        );
    }

    public Result submitRequest(Http.Request request) {

        Long userId =
                Long.parseLong(
                        request.session()
                                .getOptional("userId")
                                .orElse("0")
                );

//        String email =
//                request.session()
//                        .getOptional("email")
//                        .orElse(null);

        System.out.println("Submit Request Controller - userID - " + userId);
//        System.out.println("Submit Request Controller - email - " + email);

        Map<String,String[]> data =
                request.body().asFormUrlEncoded();

        String bloodGroup =
                data.get("bloodGroup")[0];

        String units =
                data.get("units")[0];

        User user =
                User.find.byId(userId);

        BloodTransaction tx =
                new BloodTransaction();

        tx.setUser(user);

        tx.setBloodGroup(
                bloodGroup
        );

        tx.setUnits(
                Integer.parseInt(units)
        );

        tx.setTransactionType(
                "OUTGOING"
        );

        tx.setStatus(
                "PENDING"
        );

        tx.setTransactionDate(
                java.time.LocalDateTime.now()
        );

        tx.save();

        return ok(
                "Request Submitted"
        );
    }

    public Result myTransactions(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"HOSPITAL".equals(role)) {
            return forbidden("Access Denied");
        }

        Long userId =
                Long.parseLong(
                        request.session()
                                .getOptional("userId")
                                .orElse("0")
                );

        List<BloodTransaction> transactions =
                BloodTransaction.find.query()
                        .where()
                        .eq("user.id", userId)
                        .findList();

        return ok(
                views.html.myTransactions.render(
                        transactions
                )
        );
    }
}