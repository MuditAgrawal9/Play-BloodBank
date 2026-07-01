package controllers;

import actions.DonorOnly;
import actions.JwtAuthenticated;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.*;
import security.JwtAttrs;
import services.DonorService;

@JwtAuthenticated
@DonorOnly
public class DonorController extends Controller {

  private final DonorService donorService = new DonorService();

  public Result profile(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    try {

      return ok(donorService.profile(userId));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result updateProfile(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    try {

      JsonNode body = request.body().asJson();

      String bloodGroup = body.get("bloodGroup").asText();

      int age = body.get("age").asInt();

      String phone = body.get("phone").asText();

      String city = body.get("city").asText();

      String address = body.get("address").asText();

      String pincode = body.get("pincode").asText();

      donorService.updateProfile(userId, bloodGroup, age, phone, city, address, pincode);

      return ok(Json.newObject().put("message", "Profile updated successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result donationHistory(Http.Request request) {

    Long userId = request.attrs().get(JwtAttrs.USER_ID);

    return ok(Json.toJson(donorService.donationHistory(userId)));
  }
}
