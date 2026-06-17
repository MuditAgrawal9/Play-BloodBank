package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.BloodInventory;
import models.BloodTransaction;
import models.Donor;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public Result approve(Long id,
                          Http.Request request) {

        BloodTransaction tx =
                BloodTransaction.find.byId(id);

        if(tx == null) {
            return notFound();
        }

        if(!"PENDING".equals(tx.getStatus())) {
            return badRequest("Request already processed");
        }

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq("bloodGroup",
                                tx.getBloodGroup())
                        .findOne();

        if(inventory == null) {
            return badRequest(
                    "Inventory not found"
            );
        }

        if(inventory.getUnitsAvailable()
                < tx.getUnits()) {

            return badRequest(
                    "Insufficient inventory"
            );
        }

        try(Transaction txn = DB.beginTransaction()){
            inventory.setUnitsAvailable(
                    inventory.getUnitsAvailable()
                            - tx.getUnits()
            );

            inventory.update();

            tx.setStatus("APPROVED");
            tx.update();

            txn.commit();

            logger.info(
                    "Transaction {} approved. BloodGroup={} Units={}",
                    tx.getId(),tx.getBloodGroup(),tx.getUnits()
            );
        }
        catch (Exception e){
            logger.error(
                    "Failed to approve transaction {}",
                    tx.getId(),
                    e
            );
            return badRequest("Unable to approve request");
        }

        return redirect(
                "/admin/transactions"
        );
    }

    public Result reject(Long id, Http.Request request) {

        BloodTransaction tx =
                BloodTransaction.find.byId(id);

        if(tx == null) {
            return notFound();
        }

        if(!"PENDING".equals(tx.getStatus())) {
            return badRequest("Request already processed");
        }

        try(Transaction txn = DB.beginTransaction()){
            tx.setStatus("REJECTED");
            tx.update();

            txn.commit();
            logger.info(
                    "Transaction {} rejected by admin",
                    tx.getId()
            );
        } catch (Exception e) {
            logger.error(
                    "Failed to reject transaction {}",
                    tx.getId(),
                    e
            );
            return badRequest("Unable to reject request");
        }

        return redirect("/admin/transactions");
    }

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

    public Result recordDonation(Http.Request request) {

        Map<String,String[]> data =
                request.body().asFormUrlEncoded();

        Long donorId =
                Long.parseLong(
                        data.get("donorId")[0]
                );

        Integer units =
                Integer.parseInt(
                        data.get("units")[0]
                );

        if(units <= 0) {
            return badRequest(
                    "Units must be greater than zero"
            );
        }

        Donor donor =
                Donor.find.byId(donorId);

        if(donor == null) {
            return badRequest("Donor not found");
        }

        BloodTransaction tx =
                new BloodTransaction();

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq(
                                "bloodGroup",
                                donor.getBloodGroup()
                        )
                        .findOne();

        try(Transaction txn = DB.beginTransaction()){
            tx.setUser(donor.getUser());
            tx.setBloodGroup(donor.getBloodGroup());
            tx.setUnits(units);
            tx.setTransactionType("INCOMING");
            tx.setStatus("COMPLETED");
            tx.setTransactionDate(java.time.LocalDateTime.now());
            tx.save();

            if(inventory != null) {

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
        }
        catch(Exception e) {

            logger.error(
                    "Failed to record donation for donor {}",
                    donor.getId(),
                    e
            );

            return internalServerError(
                    "Unable to record donation"
            );
        }

        return redirect(
                "/admin/transactions"
        );
    }

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
}