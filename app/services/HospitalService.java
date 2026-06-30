package services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.User;
import play.libs.Json;
import repositories.TransactionRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class HospitalService {
    private final UserRepository userRepository = new UserRepository();

    private final TransactionRepository transactionRepository = new TransactionRepository();

    public ObjectNode profile(Long id) {

        User hospital = userRepository.findById(id);

        if (hospital == null) {
            throw new RuntimeException("Hospital not found");
        }

        ObjectNode result = Json.newObject();

        result.put("id", hospital.getId());

        result.put("name", hospital.getName());

        result.put("email", hospital.getEmail());

        result.put("role", hospital.getRole().name());

        return result;
    }

    public void updateProfile(Long id, String name) {

        User hospital = userRepository.findById(id);

        if (hospital == null) {
            throw new RuntimeException("Hospital not found");
        }

        hospital.setName(name);

        userRepository.update(hospital);
    }

    public void createRequest(Long id, String bloodGroup, Integer units) {

        User hospital = userRepository.findById(id);

        if (hospital == null) {
            throw new RuntimeException("Hospital not found");
        }

        BloodTransaction request = new BloodTransaction();

        request.setUser(hospital);

        request.setBloodGroup(bloodGroup);

        request.setUnits(units);

        request.setStatus("PENDING");

        request.setTransactionType("OUTGOING");

        request.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(request);
    }

    public List<BloodTransaction> getRequests(Long id) {

        return transactionRepository.findByUserId(id);
    }

}
