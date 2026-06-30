package services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Donor;
import models.User;
import models.enums.Role;
import org.mindrot.jbcrypt.BCrypt;
import play.libs.Json;
import repositories.UserRepository;
import utils.JwtUtil;

public class AuthService {
  private final UserRepository userRepository = new UserRepository();

  public void signup(String name, String email, String password, Role role) {

    User existingUser = userRepository.findByEmail(email);

    if (existingUser != null) {

      throw new RuntimeException("Email already registered");
    }

    User user = new User();

    user.setName(name);

    user.setEmail(email);

    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

    user.setRole(role);

    userRepository.save(user);

    if (role == Role.DONOR) {

      Donor donor = new Donor();

      donor.setUser(user);

      donor.save();
    }
  }

  public ObjectNode login(String email, String password) {

    User user = userRepository.findByEmail(email);

    if (user == null) {

      throw new RuntimeException("User not found");
    }

    if (!BCrypt.checkpw(password, user.getPassword())) {

      throw new RuntimeException("Invalid credentials");
    }

    String token = JwtUtil.generateToken(user.getId(), user.getRole());

    return Json.newObject()
        .put("token", token)
        .put("id", user.getId())
        .put("name", user.getName())
        .put("email", user.getEmail())
        .put("role", user.getRole().name())
        .put("message", "Logged In Successfully");
  }
}
