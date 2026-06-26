package controllers;

import actions.HospitalOnly;
import actions.JwtAuthenticated;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.User;
import play.libs.Json;
import play.mvc.*;
import security.JwtAttrs;
import services.HospitalService;

import java.util.List;
import java.util.Map;

@JwtAuthenticated
@HospitalOnly
public class HospitalController extends Controller {

  private final HospitalService hospitalService = new HospitalService();

  public Result profile(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    try {

      return ok(hospitalService.profile(userId));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result updateProfile(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    try {

      JsonNode body = request.body().asJson();

      hospitalService.updateProfile(userId, body.get("name").asText());

      return ok(Json.newObject().put("message", "Profile updated successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result createRequest(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    try {

      JsonNode body = request.body().asJson();

      hospitalService.createRequest(
              userId, body.get("bloodGroup").asText(), body.get("unitsRequired").asInt());

      return ok(Json.newObject().put("message", "Request submitted successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result getRequests(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    return ok(Json.toJson(hospitalService.getRequests(userId)));
  }
}
