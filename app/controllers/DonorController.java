package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.Donor;
import models.User;
import play.libs.Json;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class DonorController extends Controller {

    public Result profile(Long id) {

        User user = User.find.byId(id);

        if (user == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "User not found"
                            )
            );
        }

        Donor donor =
                Donor.find.query()
                        .where()
                        .eq(
                                "user.id",
                                user.getId()
                        )
                        .findOne();

        if (donor == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Donor not found"
                            )
            );
        }

        ObjectNode result =
                Json.newObject();

        result.put(
                "name",
                user.getName()
        );

        result.put(
                "email",
                user.getEmail()
        );

        result.put(
                "bloodGroup",
                donor.getBloodGroup()
        );

        result.put(
                "age",
                donor.getAge()
        );

        result.put(
                "phone",
                user.getPhone()
        );

        result.put(
                "city",
                user.getCity()
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

        String bloodGroup =
                body.get("bloodGroup")
                        .asText();

        int age =
                body.get("age")
                        .asInt();

        String phone =
                body.get("phone")
                        .asText();

        String city =
                body.get("city")
                        .asText();

        User user = User.find.byId(id);

        Donor donor =
                Donor.find.query()
                        .where()
                        .eq(
                                "user.id",
                                id
                        )
                        .findOne();

        if (donor == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Donor not found"
                            )
            );
        }

        donor.setBloodGroup(
                bloodGroup
        );

        donor.setAge(
                age
        );

        user.setPhone(
                phone
        );

        user.setCity(
                city
        );

        donor.update();
        user.update();

        return ok(
                Json.newObject()
                        .put(
                                "message",
                                "Profile updated successfully"
                        )
        );
    }

    public Result donationHistory(Long id) {

        List<BloodTransaction> transactions =
                BloodTransaction.find.query()
                        .where()
                        .eq("user.id", id)
                        .eq("transactionType", "INCOMING")
                        .orderBy("transactionDate desc")
                        .findList();

        return ok(
                Json.toJson(transactions)
        );
    }

}