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

    public Result dashboard(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"ADMIN".equals(role)) {
            return forbidden("Access Denied");
        }

        long donorCount =
                Donor.find.query().findCount();

        long hospitalCount =
                User.find.query()
                        .where()
                        .eq("role", "HOSPITAL")
                        .findCount();

        long pendingRequests =
                BloodTransaction.find.query()
                        .where()
                        .eq("status", "PENDING")
                        .findCount();

        long transactionCount =
                BloodTransaction.find.query().findCount();

        return ok(
                views.html.adminDashboard.render(
                        donorCount,
                        hospitalCount,
                        pendingRequests,
                        transactionCount
                )
        );
    }

    public Result transactions(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"ADMIN".equals(role)) {
            return forbidden("Access Denied");
        }

        List<BloodTransaction> transactions =
                BloodTransaction.find.all();

        return ok(
                views.html.adminTransactions.render(
                        transactions
                )
        );
    }

//    public Result approve(Long id,
//                          Http.Request request) {
//
//        logger.info("HELLO");
//        BloodTransaction tx =
//                BloodTransaction.find.byId(id);
//
//        if(tx == null) {
//            return notFound();
//        }
//
//        if(!"PENDING".equals(tx.getStatus())) {
//            return badRequest("Request already processed");
//        }
//
//        BloodInventory inventory =
//                BloodInventory.find.query()
//                        .where()
//                        .eq("bloodGroup",
//                                tx.getBloodGroup())
//                        .findOne();
//
//        if(inventory == null) {
//            return badRequest(
//                    "Inventory not found"
//            );
//        }
//
//        if(inventory.getUnitsAvailable()
//                < tx.getUnits()) {
//
//            return badRequest(
//                    "Insufficient inventory"
//            );
//        }
//
//        try(Transaction txn = DB.beginTransaction()){
//            inventory.setUnitsAvailable(
//                    inventory.getUnitsAvailable()
//                            - tx.getUnits()
//            );
//
//            inventory.update();
//
//            tx.setStatus("APPROVED");
//            tx.update();
//
//            txn.commit();
//
//            logger.info(
//                    "Transaction {} approved. BloodGroup={} Units={}",
//                    tx.getId(),tx.getBloodGroup(),tx.getUnits()
//            );
//        }
//        catch (Exception e){
//            logger.error(
//                    "Failed to approve transaction {}",
//                    tx.getId(),
//                    e
//            );
//            return badRequest("Unable to approve request");
//        }
//
//        return redirect(
//                "/admin/transactions"
//        );
//    }

//    public Result reject(Long id, Http.Request request) {
//
//        BloodTransaction tx =
//                BloodTransaction.find.byId(id);
//
//        if(tx == null) {
//            return notFound();
//        }
//
//        if(!"PENDING".equals(tx.getStatus())) {
//            return badRequest("Request already processed");
//        }
//
//        try(Transaction txn = DB.beginTransaction()){
//            tx.setStatus("REJECTED");
//            tx.update();
//
//            txn.commit();
//            logger.info(
//                    "Transaction {} rejected by admin",
//                    tx.getId()
//            );
//        } catch (Exception e) {
//            logger.error(
//                    "Failed to reject transaction {}",
//                    tx.getId(),
//                    e
//            );
//            return badRequest("Unable to reject request");
//        }
//
//        return redirect("/admin/transactions");
//    }
//
    public Result donationPage(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"ADMIN".equals(role)) {
            return forbidden("Access Denied");
        }

        List<Donor> donors =
                Donor.find.all();

        return ok(
                views.html.recordDonation.render(
                        donors
                )
        );
    }

//    public Result recordDonation(Http.Request request) {
//
//        Map<String,String[]> data =
//                request.body().asFormUrlEncoded();
//
//        Long donorId =
//                Long.parseLong(
//                        data.get("donorId")[0]
//                );
//
//        Integer units =
//                Integer.parseInt(
//                        data.get("units")[0]
//                );
//
//        if(units <= 0) {
//            return badRequest(
//                    "Units must be greater than zero"
//            );
//        }
//
//        Donor donor =
//                Donor.find.byId(donorId);
//
//        if(donor == null) {
//            return badRequest("Donor not found");
//        }
//
//        BloodTransaction tx =
//                new BloodTransaction();
//
//        BloodInventory inventory =
//                BloodInventory.find.query()
//                        .where()
//                        .eq(
//                                "bloodGroup",
//                                donor.getBloodGroup()
//                        )
//                        .findOne();
//
//        try(Transaction txn = DB.beginTransaction()){
//            tx.setUser(donor.getUser());
//            tx.setBloodGroup(donor.getBloodGroup());
//            tx.setUnits(units);
//            tx.setTransactionType("INCOMING");
//            tx.setStatus("COMPLETED");
//            tx.setTransactionDate(java.time.LocalDateTime.now());
//            tx.save();
//
//            if(inventory != null) {
//
//                inventory.setUnitsAvailable(
//                        inventory.getUnitsAvailable()
//                                + units
//                );
//
//                inventory.update();
//            }
//
//            txn.commit();
//
//            logger.info(
//                    "Donation recorded. Donor={} BloodGroup={} Units={}",
//                    donor.getId(),
//                    donor.getBloodGroup(),
//                    units
//            );
//        }
//        catch(Exception e) {
//
//            logger.error(
//                    "Failed to record donation for donor {}",
//                    donor.getId(),
//                    e
//            );
//
//            return internalServerError(
//                    "Unable to record donation"
//            );
//        }
//
//        return redirect(
//                "/admin/transactions"
//        );
//    }

    public Result inventory(Http.Request request) {

        String role =
                request.session()
                        .getOptional("role")
                        .orElse(null);

        if(!"ADMIN".equals(role)) {
            return forbidden("Access Denied");
        }

        List<BloodInventory> inventory =
                BloodInventory.find.all();

        return ok(
                views.html.inventory.render(
                        inventory
                )
        );
    }

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
//
//        int pendingDonations =
//                BloodTransaction.find.query()
//                        .where()
//                        .eq("transactionType", "INCOMING")
//                        .eq("status", "PENDING")
//                        .findCount();
//
//        int totalUnits =
//                BloodInventory.find.query()
//                        .findList()
//                        .stream()
//                        .mapToInt(
//                                BloodInventory::getUnitsAvailable
//                        )
//                        .sum();

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

//        List<User> donors = User.find.query()
//                .where()
//                .eq("role", "DONOR")
//                .findList();
//
//        return ok(Json.toJson(donors));

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

    public Result recordDonation(
            Http.Request request
    ) {

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