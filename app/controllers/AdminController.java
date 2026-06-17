package controllers;

import models.BloodInventory;
import models.BloodTransaction;
import models.Donor;
import models.User;
import play.mvc.*;

import java.util.List;
import java.util.Map;

public class AdminController extends Controller {

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

        inventory.setUnitsAvailable(
                inventory.getUnitsAvailable()
                        - tx.getUnits()
        );

        inventory.update();

        tx.setStatus("APPROVED");
        tx.update();

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

        tx.setStatus("REJECTED");
        tx.update();

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

        Donor donor =
                Donor.find.byId(donorId);

        if(donor == null) {
            return badRequest("Donor not found");
        }

        BloodTransaction tx =
                new BloodTransaction();

        tx.setUser(
                donor.getUser()
        );

        tx.setBloodGroup(
                donor.getBloodGroup()
        );

        tx.setUnits(units);

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

        BloodInventory inventory =
                BloodInventory.find.query()
                        .where()
                        .eq(
                                "bloodGroup",
                                donor.getBloodGroup()
                        )
                        .findOne();

        if(inventory != null) {

            inventory.setUnitsAvailable(
                    inventory.getUnitsAvailable()
                            + units
            );

            inventory.update();
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