package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.AuthService;

public class AuthController extends Controller {

  private final AuthService authService = new AuthService();

  public Result signup(Http.Request request) {

    try {

      JsonNode body = request.body().asJson();

      authService.signup(
          body.get("name").asText(),
          body.get("email").asText(),
          body.get("password").asText(),
          body.get("role").asText());

      return ok(Json.newObject().put("message", "Signup successful"));

    } catch (Exception e) {

      return badRequest(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result login(Http.Request request) {

    try {

      JsonNode body = request.body().asJson();

      return ok(authService.login(body.get("email").asText(), body.get("password").asText()));

    } catch (Exception e) {

      return unauthorized(Json.newObject().put("message", e.getMessage()));
    }
  }

  public Result logout(Http.Request request) {

    return ok(Json.newObject().put("message", "Logged out successfully"));
  }
}
