package controllers;

import models.User;
import play.mvc.*;

import java.util.Map;

public class AuthController extends Controller {

    public Result signupPage() {
        return ok(
                views.html.signup.render()
        );
    }

    public Result signup(Http.Request request) {

        Map<String,String[]> data =
                request.body().asFormUrlEncoded();

        String name =
                data.get("name")[0];

        String email =
                data.get("email")[0];

        String password =
                data.get("password")[0];

        User existingUser =
                User.find.query()
                        .where()
                        .eq("email", email)
                        .findOne();

        if(existingUser != null) {
            return badRequest(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("DONOR");

        user.save();

//        return ok("Signup Successful");
        return redirect("/login");
    }

    public Result loginPage() {
        return ok(
                views.html.login.render()
        );
    }

    public Result login(Http.Request request) {

        Map<String,String[]> data =
                request.body().asFormUrlEncoded();

        String email =
                data.get("email")[0];

        String password =
                data.get("password")[0];

        User user =
                User.find.query()
                        .where()
                        .eq("email", email)
                        .eq("password", password)
                        .findOne();

        if(user == null) {

            return unauthorized(
                    "Invalid Credentials"
            );

        }

        return redirect("/")
                .addingToSession(
                        request,
                        "email",
                        user.getEmail()
                );
    }

    public Result dashboard(Http.Request request) {

        String email =
                request.session()
                        .getOptional("email")
                        .orElse(null);

        if(email == null) {
            return redirect("/login");
        }

//        return ok("Welcome " + email );
        return redirect("/profile");
    }
}