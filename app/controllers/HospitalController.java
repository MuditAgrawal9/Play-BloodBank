package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.User;
import play.libs.Json;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class HospitalController extends Controller {

    public Result profile(Long id) {

        User hospital =
                User.find.byId(id);

        if (hospital == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Hospital not found"
                            )
            );
        }

        ObjectNode result =
                Json.newObject();

        result.put(
                "id",
                hospital.getId()
        );

        result.put(
                "name",
                hospital.getName()
        );

        result.put(
                "email",
                hospital.getEmail()
        );

        result.put(
                "role",
                hospital.getRole()
        );

        return ok(result);
    }

    public Result updateProfile(
            Long id,
            Http.Request request
    ) {

        JsonNode body =
                request.body()
                        .asJson();

        User hospital =
                User.find.byId(id);

        if (hospital == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Hospital not found"
                            )
            );
        }

        hospital.setName(
                body.get("name")
                        .asText()
        );

        hospital.update();

        return ok(
                Json.newObject()
                        .put(
                                "message",
                                "Profile updated successfully"
                        )
        );
    }

    public Result createRequest(
            Long id,
            Http.Request request
    ) {

        JsonNode body =
                request.body()
                        .asJson();

        User hospital =
                User.find.byId(id);

        if(hospital == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Hospital not found"
                            )
            );
        }

        BloodTransaction bloodRequest =
                new BloodTransaction();

        bloodRequest.setUser(
                hospital
        );

        bloodRequest.setBloodGroup(
                body.get("bloodGroup")
                        .asText()
        );

        bloodRequest.setUnits(
                body.get("unitsRequired")
                        .asInt()
        );

        bloodRequest.setStatus(
                "PENDING"
        );

        bloodRequest.setTransactionType(
                "OUTGOING"
        );


        bloodRequest.setTransactionDate(
                java.time.LocalDateTime.now()
        );

        bloodRequest.save();

        return ok(
                Json.newObject()
                        .put(
                                "message",
                                "Request submitted successfully"
                        )
        );
    }

    public Result getRequests(Long id) {

        List<BloodTransaction> requests =
                BloodTransaction.find.query()
                        .where()
                        .eq(
                                "user.id",
                                id
                        )
                        .orderBy(
                                "transaction_date desc"
                        )
                        .findList();

        return ok(
                Json.toJson(
                        requests
                )
        );
    }
}