package services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.BloodTransaction;
import models.Donor;
import models.User;
import play.libs.Json;
import repositories.DonorRepository;
import repositories.TransactionRepository;
import repositories.UserRepository;

import java.util.List;

public class DonorService {
    private final UserRepository userRepository = new UserRepository();

    private final DonorRepository donorRepository = new DonorRepository();

    private final TransactionRepository transactionRepository = new TransactionRepository();

    public ObjectNode profile(Long id) {

        User user = userRepository.findById(id);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Donor donor = donorRepository.findByUserId(id);

        if (donor == null) {
            throw new RuntimeException("Donor not found");
        }

        ObjectNode result = Json.newObject();

        result.put("name", user.getName());

        result.put("email", user.getEmail());

        result.put("bloodGroup", donor.getBloodGroup());

        result.put("age", donor.getAge());

        result.put("phone", user.getPhone());

        result.put("city", user.getCity());

        return result;
    }

    public void updateProfile(Long id, String bloodGroup, int age, String phone, String city) {

        User user = userRepository.findById(id);

        Donor donor = donorRepository.findByUserId(id);

        if (donor == null) {
            throw new RuntimeException("Donor not found");
        }

        donor.setBloodGroup(bloodGroup);

        donor.setAge(age);

        user.setPhone(phone);

        user.setCity(city);

        donorRepository.update(donor);

        userRepository.update(user);
    }

    public List<BloodTransaction> donationHistory(Long id) {

        return transactionRepository.findIncomingByUserId(id);
    }
}
