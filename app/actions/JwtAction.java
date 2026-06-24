package actions;

import io.jsonwebtoken.Claims;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import security.JwtAttrs;
import utils.JwtUtil;

import java.util.concurrent.CompletionStage;

import static play.mvc.Results.unauthorized;

public class JwtAction extends Action.Simple {

  @Override
  public CompletionStage<Result> call(Http.Request request) {

    String authHeader = request.getHeaders().get("Authorization").orElse(null);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {

      return delegate.call(request).thenApply(result -> unauthorized("Missing token"));
    }

    try {

      String token = authHeader.substring(7);

      Claims claims = JwtUtil.validateToken(token);

      Long userId = Long.parseLong(claims.getSubject());

      String role = claims.get("role", String.class);

      Http.Request newRequest =
          request.addAttr(JwtAttrs.USER_ID, userId).addAttr(JwtAttrs.ROLE, role);

      return delegate.call(newRequest);

    } catch (Exception e) {

      return delegate.call(request).thenApply(result -> unauthorized("Invalid token"));
    }
  }
}
