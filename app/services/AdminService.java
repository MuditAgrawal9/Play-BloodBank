package services;

import io.ebean.DB;
import io.ebean.Transaction;
import models.BloodInventory;
import models.BloodTransaction;
import models.Donor;
import repositories.DonorRepository;
import repositories.InventoryRepository;
import repositories.TransactionRepository;

public class AdminService {

  private final TransactionRepository transactionRepository = new TransactionRepository();

  private final InventoryRepository inventoryRepository = new InventoryRepository();

  private final DonorRepository donorRepository = new DonorRepository();

  public void approveTransaction(Long id) {

    BloodTransaction tx = transactionRepository.findById(id);

    if (tx == null) {
      throw new RuntimeException("Transaction not found");
    }

    BloodInventory inventory = inventoryRepository.findByBloodGroup(tx.getBloodGroup());

    if (inventory == null) {
      throw new RuntimeException("Inventory not found");
    }

    if (inventory.getUnitsAvailable() < tx.getUnits()) {

      throw new RuntimeException("Insufficient inventory");
    }

    try (Transaction txn = DB.beginTransaction()) {

      inventory.setUnitsAvailable(inventory.getUnitsAvailable() - tx.getUnits());

      inventoryRepository.update(inventory);

      tx.setStatus("APPROVED");

      transactionRepository.update(tx);

      txn.commit();
    }
  }

  public void rejectTransaction(Long id) {

    BloodTransaction tx = transactionRepository.findById(id);

    if (tx == null) {
      throw new RuntimeException("Transaction not found");
    }

    if (!"PENDING".equals(tx.getStatus())) {

      throw new RuntimeException("Transaction already processed");
    }

    tx.setStatus("REJECTED");

    transactionRepository.update(tx);
  }

  public void recordDonation(Long donorId, Integer units) {

    if (units <= 0) {

      throw new RuntimeException("Units must be greater than zero");
    }

    Donor donor = donorRepository.findById(donorId);

    if (donor == null) {

      throw new RuntimeException("Donor not found");
    }

    BloodInventory inventory = inventoryRepository.findByBloodGroup(donor.getBloodGroup());

    try (Transaction txn = DB.beginTransaction()) {

      BloodTransaction tx = new BloodTransaction();

      tx.setUser(donor.getUser());

      tx.setBloodGroup(donor.getBloodGroup());

      tx.setUnits(units);

      tx.setTransactionType("INCOMING");

      tx.setStatus("COMPLETED");

      tx.setTransactionDate(java.time.LocalDateTime.now());

      transactionRepository.save(tx);

      if (inventory != null) {

        inventory.setUnitsAvailable(inventory.getUnitsAvailable() + units);

        inventoryRepository.update(inventory);
      }

      txn.commit();

    } catch (Exception e) {

      throw new RuntimeException("Unable to record donation");
    }
  }
}
