package controllers;

import actions.JwtAuthenticated;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.User;
import play.libs.Json;
import play.mvc.*;
import services.HospitalService;

import java.util.List;
import java.util.Map;

@JwtAuthenticated
public class HospitalController extends Controller {

    private final HospitalService hospitalService = new HospitalService();
    
    public Result profile(Long id) {

        try {

            return ok(hospitalService.profile(id));

        } catch (Exception e) {

            return badRequest(Json.newObject().put("message", e.getMessage()));
        }
    }

    public Result updateProfile(Long id, Http.Request request) {

        try {

            JsonNode body = request.body().asJson();

            hospitalService.updateProfile(id, body.get("name").asText());

            return ok(Json.newObject().put("message", "Profile updated successfully"));

        } catch (Exception e) {

            return badRequest(Json.newObject().put("message", e.getMessage()));
        }
    }

    public Result createRequest(Long id, Http.Request request) {

        try {

            JsonNode body = request.body().asJson();

            hospitalService.createRequest(id, body.get("bloodGroup").asText(), body.get("unitsRequired").asInt());

            return ok(Json.newObject().put("message", "Request submitted successfully"));

        } catch (Exception e) {

            return badRequest(Json.newObject().put("message", e.getMessage()));
        }
    }

    public Result getRequests(Long id) {

        return ok(Json.toJson(hospitalService.getRequests(id)));
    }
}