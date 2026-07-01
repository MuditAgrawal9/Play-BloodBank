package controllers;

import actions.AdminOnly;
import actions.JwtAuthenticated;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.*;
import services.AdminGetService;
import services.AdminService;

@JwtAuthenticated
@AdminOnly
public class AdminController extends Controller {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  private final AdminService adminService = new AdminService();

  private final AdminGetService adminGetService = new AdminGetService();

  public Result getStats() {
    return ok(adminGetService.getStats());
  }

  public Result getDonors() {
    return ok(adminGetService.getDonors());
  }

  public Result getUsers() {
    return ok(Json.toJson(adminGetService.getUsers()));
  }

  public Result getHospitals() {
    return ok(Json.toJson(adminGetService.getHospitals()));
  }

  public Result getInventory() {
    return ok(Json.toJson(adminGetService.getInventory()));
  }

  public Result getTransactions() {
    return ok(Json.toJson(adminGetService.getTransactions()));
  }

  public Result approve(Long id) {

    try {

      adminService.approveTransaction(id);

      return ok(Json.newObject().put("message", "Transaction approved successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result reject(Long id) {

    try {

      adminService.rejectTransaction(id);

      return ok(Json.newObject().put("message", "Transaction rejected successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result recordDonation(Http.Request request) {

    try {

      JsonNode body = request.body().asJson();

      Long donorId = body.get("donorId").asLong();

      Integer units = body.get("units").asInt();

      adminService.recordDonation(donorId, units);

      return ok(Json.newObject().put("message", "Donation recorded successfully"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }
}