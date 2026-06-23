package repositories;

import models.BloodTransaction;

import java.util.List;

public class TransactionRepository {

    public BloodTransaction findById(Long id) {
        return BloodTransaction.find.byId(id);
    }

    public List<BloodTransaction> findAll() {
        return BloodTransaction.find.query().orderBy("transactionDate desc").findList();
    }

    public void save(BloodTransaction tx) {
        tx.save();
    }

    public void update(BloodTransaction tx) {
        tx.update();
    }

    public int countPendingTransactions() {

        return BloodTransaction.find.query().where().eq("status", "PENDING").findCount();
    }

}