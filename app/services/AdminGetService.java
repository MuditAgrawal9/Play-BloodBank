package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodInventory;
import models.BloodTransaction;
import models.Donor;
import models.User;
import play.libs.Json;
import repositories.DonorRepository;
import repositories.InventoryRepository;
import repositories.TransactionRepository;
import repositories.UserRepository;

import java.util.List;

public class AdminGetService {

    private final UserRepository userRepository = new UserRepository();

    private final DonorRepository donorRepository = new DonorRepository();

    private final InventoryRepository inventoryRepository = new InventoryRepository();

    private final TransactionRepository transactionRepository = new TransactionRepository();

    public ObjectNode getStats() {

        ObjectNode result = Json.newObject();

        result.put("totalUsers", userRepository.countUsers());

        result.put("totalDonors", userRepository.countDonors());

        result.put("totalHospitals", userRepository.countHospitals());

        result.put("pendingRequests", transactionRepository.countPendingTransactions());

        return result;
    }

    public List<User> getUsers() {

        return userRepository.findAll();
    }

    public ArrayNode getDonors() {

        ArrayNode array = Json.newArray();

        List<Donor> donors = donorRepository.findAll();

        for (Donor donor : donors) {

            ObjectNode node = Json.newObject();

            node.put("id", donor.getId());

            node.put("bloodGroup", donor.getBloodGroup());

            node.put("age", donor.getAge());

//            node.put("phone", donor.getPhone());
//
//            node.put("city", donor.getCity());

            node.set("user", Json.toJson(donor.getUser()));

            array.add(node);
        }

        return array;
    }

    public List<User> getHospitals() {

        return userRepository.findHospitals();
    }

    public List<BloodInventory> getInventory() {

        return inventoryRepository.findAll();
    }

    public List<BloodTransaction> getTransactions() {

        return transactionRepository.findAll();
    }
}