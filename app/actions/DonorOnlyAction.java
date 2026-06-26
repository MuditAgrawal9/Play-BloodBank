package actions;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import security.JwtAttrs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.forbidden;

public class DonorOnlyAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {

        String role =
                request.attrs()
                        .get(JwtAttrs.ROLE);

        if (!"DONOR".equals(role)) {

            return CompletableFuture.completedFuture(
                    forbidden("Donor access required")
            );
        }

        return delegate.call(request);
    }
}