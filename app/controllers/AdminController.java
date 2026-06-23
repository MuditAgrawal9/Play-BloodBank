package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.DB;
import io.ebean.Transaction;
import models.BloodInventory;
import models.BloodTransaction;
import models.Donor;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class AdminController extends Controller {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    AdminController.class
            );

    public Result getStats() {

//        logger.info("Entered getStats");
        int totalUsers =
                User.find.query()
                        .findCount();

        int totalDonors =
                User.find.query()
                        .where()
                        .eq("role", "DONOR")
                        .findCount();

        int totalHospitals =
                User.find.query()
                        .where()
                        .eq("role", "HOSPITAL")
                        .findCount();

        int pendingRequests =
                BloodTransaction.find.query()
                        .where()
                        .eq("status", "PENDING")
                        .findCount();

        ObjectNode result =
                Json.newObject();

        result.put(
                "totalUsers",
                totalUsers
        );

        result.put(
                "totalDonors",
                totalDonors
        );

        result.put(
                "totalHospitals",
                totalHospitals
        );

        result.put(
                "pendingRequests",
                pendingRequests
        );
//
//        result.put(
//                "pendingDonations",
//                pendingDonations
//        );
//
//        result.put(
//                "totalUnits",
//                totalUnits
//        );

        return ok(result);
    }

    public Result getDonors() {

        ArrayNode array = Json.newArray();

        for (Donor donor : Donor.find.all()) {
            ObjectNode node = Json.newObject();

            node.put("id", donor.getId());
            node.put("bloodGroup", donor.getBloodGroup());
            node.put("age", donor.getAge());

            node.set("user", Json.toJson(donor.getUser()));
            array.add(node);
        }

        return ok(array);
    }

    public Result getUsers() {

        List<User> users =
                User.find.all();

        return ok(
                Json.toJson(users)
        );
    }

    public Result getHospitals() {

        List<User> hospitals = User.find.query()
                .where()
                .eq("role", "HOSPITAL")
                .findList();

        return ok(Json.toJson(hospitals));
    }

    public Result getInventory() {

        List<BloodInventory> inventory =
                BloodInventory.find.all();

        return ok(
                Json.toJson(inventory)
        );
    }

    public Result getTransactions() {

        List<BloodTransaction> transactions =
                BloodTransaction.find.query().orderBy("transaction_date desc").findList();

        return ok(
                Json.toJson(transactions)
        );
    }

    public Result reject(Long id) {

        logger.info(
                "Approve request received for transaction {}",
                id
        );

        BloodTransaction tx =
                BloodTransaction.find.byId(id);

        if (tx == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction not found"
                            )
            );
        }

        if (!"PENDING".equals(
                tx.getStatus()
        )) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction already processed"
                            )
            );
        }

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq(
                                "bloodGroup",
                                tx.getBloodGroup()
                        )
                        .findOne();

        if (inventory == null) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Inventory not found"
                            )
            );
        }

        try (
                Transaction txn =
                        DB.beginTransaction()
        ) {

            tx.setStatus(
                    "REJECTED"
            );

            tx.update();

            txn.commit();

            logger.info(
                    "Transaction {} rejected.",
                    tx.getId()
            );

            return ok(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction approved successfully"
                            )
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to reject transaction {}",
                    id,
                    e
            );

            return internalServerError(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Unable to approve transaction"
                            )
            );
        }
    }

    public Result approve(Long id) {

        logger.info(
                "Reject request received for transaction {}",
                id
        );

        BloodTransaction tx =
                BloodTransaction.find.byId(id);

        if (tx == null) {

            return notFound(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction not found"
                            )
            );
        }

        if (!"PENDING".equals(
                tx.getStatus()
        )) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction already processed"
                            )
            );
        }

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq(
                                "bloodGroup",
                                tx.getBloodGroup()
                        )
                        .findOne();

        if (inventory == null) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Inventory not found"
                            )
            );
        }

        if (inventory.getUnitsAvailable()
                < tx.getUnits()) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Insufficient inventory"
                            )
            );
        }

        try (
                Transaction txn =
                        DB.beginTransaction()
        ) {

            inventory.setUnitsAvailable(
                    inventory.getUnitsAvailable()
                            - tx.getUnits()
            );

            inventory.update();

            tx.setStatus(
                    "APPROVED"
            );

            tx.update();

            txn.commit();

            logger.info(
                    "Transaction {} approved. BloodGroup={} Units={}",
                    tx.getId(),
                    tx.getBloodGroup(),
                    tx.getUnits()
            );

            return ok(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Transaction approved successfully"
                            )
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to approve transaction {}",
                    id,
                    e
            );

            return internalServerError(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Unable to approve transaction"
                            )
            );
        }
    }

    public Result recordDonation(Http.Request request) {

        JsonNode body =
                request.body()
                        .asJson();

        Long donorId =
                body.get("donorId")
                        .asLong();

        Integer units =
                body.get("units")
                        .asInt();

        if (units <= 0) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Units must be greater than zero"
                            )
            );
        }

        Donor donor =
                Donor.find.byId(
                        donorId
                );

        if (donor == null) {

            return badRequest(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Donor not found"
                            )
            );
        }

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq(
                                "bloodGroup",
                                donor.getBloodGroup()
                        )
                        .findOne();

        try (
                Transaction txn =
                        DB.beginTransaction()
        ) {

            BloodTransaction tx =
                    new BloodTransaction();

            tx.setUser(
                    donor.getUser()
            );

            tx.setBloodGroup(
                    donor.getBloodGroup()
            );

            tx.setUnits(
                    units
            );

            tx.setTransactionType(
                    "INCOMING"
            );

            tx.setStatus(
                    "COMPLETED"
            );

            tx.setTransactionDate(
                    java.time.LocalDateTime.now()
            );

            tx.save();

            if (inventory != null) {

                inventory.setUnitsAvailable(
                        inventory.getUnitsAvailable()
                                + units
                );

                inventory.update();
            }

            txn.commit();

            logger.info(
                    "Donation recorded. Donor={} BloodGroup={} Units={}",
                    donor.getId(),
                    donor.getBloodGroup(),
                    units
            );

            return ok(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Donation recorded successfully"
                            )
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to record donation for donor {}",
                    donor.getId(),
                    e
            );

            return internalServerError(
                    Json.newObject()
                            .put(
                                    "message",
                                    "Unable to record donation"
                            )
            );
        }
    }

}