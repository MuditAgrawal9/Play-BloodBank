package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.*;

import java.util.Map;

public class AuthController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public Result signupPage() {
        return ok(views.html.signup.render());
    }

    public Result signup(Http.Request request) {
        JsonNode body = request.body().asJson();

        String name = body.get("name").asText();

        String email = body.get("email").asText();

        String password = body.get("password").asText();

        String role = body.get("role").asText();

        if(name == null || email == null || password == null){
            return badRequest(
                    Json.newObject().put("message", "Please all fields")
            );
        }

        User existingUser = User.find.query().where().eq("email", email).findOne();

        if (existingUser != null) {
            return badRequest(
                    Json.newObject().put("message", "Email already registered")
            );
        }

        User user = new User();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());


        user.setName(name);
        user.setEmail(email);
//        user.setPassword(password);
        user.setPassword(hashedPassword);
        user.setRole(role);

        user.save();

//        return ok("Signup Successful");
//        return redirect("/login");
        return ok(Json.newObject().put("message", "Signup successful"));
    }

    public Result loginPage() {
        return ok(views.html.login.render());
    }

    public Result login(Http.Request request) {
        JsonNode body = request.body().asJson();

        String email = body.get("email").asText();
        String password = body.get("password").asText();

        User user = User.find.query().where().eq("email", email).findOne();

        if (user == null) {
            return unauthorized(
                    Json.newObject()
                            .put("message", "User Not Found")
            );
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return unauthorized(
                    Json.newObject()
                            .put("message", "Invalid credentials")
            );
        }

        System.out.println("Login Controller - userId - " + user.getId());

        return ok(
                Json.newObject()
                        .put("id", user.getId())
                        .put("name", user.getName())
                        .put("email", user.getEmail())
                        .put("role", user.getRole())
                        .put("message", "Logged In Successfully"));
    }

    public Result logout(Http.Request request) {

        return redirect("/login").removingFromSession(request, "userId", "email", "role");
    }

    public Result dashboard(Http.Request request) {

        String email = request.session().getOptional("email").orElse(null);

        if (email == null) {
            return redirect("/login");
        }

        String role = request.session().getOptional("role").orElse(null);

//        return ok("Welcome " + email );
//        return redirect("/profile");


        if ("HOSPITAL".equals(role)) {
            return redirect("/hospital");
        }

        if ("ADMIN".equals(role)) {
            return redirect("/admin");
        }

        //DONOR
        return redirect("/profile");
    }

}