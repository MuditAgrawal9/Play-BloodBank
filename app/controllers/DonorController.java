package controllers;

import actions.JwtAuthenticated;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.Donor;
import models.User;
import play.libs.Json;
import play.mvc.*;
import services.DonorService;

import java.util.List;

@JwtAuthenticated
public class DonorController extends Controller {

    private final DonorService donorService = new DonorService();

    public Result profile(Long id) {

        try {

            return ok(donorService.profile(id));

        } catch (Exception e) {

            return badRequest(Json.newObject().put("message", e.getMessage()));
        }
    }

    public Result updateProfile(Long id, Http.Request request) {

        try {

            JsonNode body = request.body().asJson();

            String bloodGroup = body.get("bloodGroup").asText();

            int age = body.get("age").asInt();

            String phone = body.get("phone").asText();

            String city = body.get("city").asText();

            donorService.updateProfile(id, bloodGroup, age, phone, city);

            return ok(Json.newObject().put("message", "Profile updated successfully"));

        } catch (Exception e) {

            return badRequest(Json.newObject().put("message", e.getMessage()));
        }
    }

    public Result donationHistory(Long id) {

        return ok(Json.toJson(donorService.donationHistory(id)));
    }

}