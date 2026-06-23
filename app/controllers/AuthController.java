package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Donor;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.*;

import java.util.Map;

public class AuthController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

        if(role.equals("DONOR")){
            Donor donor = new Donor();
            donor.setUser(user);
            donor.save();
        }

//        return ok("Signup Successful");
//        return redirect("/login");
        return ok(Json.newObject().put("message", "Signup successful"));
    }

    public Result login(Http.Request request) {
        logger.info("Hello");
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

}